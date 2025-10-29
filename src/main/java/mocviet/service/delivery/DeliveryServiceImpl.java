package mocviet.service.delivery;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service; // Repository gốc
import org.springframework.transaction.annotation.Transactional; // Repositories trong package delivery

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor; // Import PasswordEncoder
import mocviet.dto.MessageResponse;
import mocviet.dto.PasswordChangeRequest;
import mocviet.dto.ProfileUpdateRequest;
import mocviet.dto.delivery.DeliveryOrderDetailDTO;
import mocviet.dto.delivery.DeliveryOrderSummaryDTO;
import mocviet.dto.delivery.DeliveryStatsDTO;
import mocviet.dto.delivery.DeliveryUpdateRequestDTO;
import mocviet.entity.DeliveryTeam;
import mocviet.entity.OrderDelivery;
import mocviet.entity.User;
import mocviet.repository.UserRepository;
import mocviet.repository.delivery.DeliveryOrderDeliveryRepository;
import mocviet.repository.delivery.DeliveryTeamRepository;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements IDeliveryService {

    private final DeliveryTeamRepository deliveryTeamRepository;
    private final DeliveryOrderDeliveryRepository orderDeliveryRepository;
    private final UserRepository userRepository; // Repo gốc để lấy User
    private final PasswordEncoder passwordEncoder; // Để đổi mật khẩu
    private final EntityManager entityManager; // Để gọi Stored Procedure
    private final DeliveryFileUploadService fileUploadService; // Để upload ảnh

    /** Helper lấy User entity từ Authentication */
    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Người dùng chưa được xác thực.");
        }
        Object principal = authentication.getPrincipal();
        return switch (principal) {
            case User user -> user;
            case String username -> userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + username));
            default -> throw new RuntimeException("Không thể xác định người dùng hiện tại.");
        };
    }
    
    
 // <<<--- THÊM PHƯƠNG THỨC NÀY --- >>>
    @Override
    @Transactional(readOnly = true)
    public DeliveryStatsDTO getDeliveryStats(Authentication authentication) {
        Integer teamId = getCurrentDeliveryTeamId(authentication);
        OrderDelivery.DeliveryStatus doneStatus = OrderDelivery.DeliveryStatus.DONE;

        // Lấy thời điểm bắt đầu của hôm nay, tuần này (Thứ 2), tháng này
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        // Gọi repository để đếm
        long todayCount = orderDeliveryRepository.countByDeliveryTeamIdAndStatusAndUpdatedAtAfter(
                teamId, doneStatus, todayStart
        );
        long weekCount = orderDeliveryRepository.countByDeliveryTeamIdAndStatusAndUpdatedAtAfter(
                teamId, doneStatus, weekStart
        );
        long monthCount = orderDeliveryRepository.countByDeliveryTeamIdAndStatusAndUpdatedAtAfter(
                teamId, doneStatus, monthStart
        );

        return new DeliveryStatsDTO(todayCount, weekCount, monthCount);
    }
    @Override
    @Transactional(readOnly = true)
    public Integer getCurrentDeliveryTeamId(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        DeliveryTeam team = deliveryTeamRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Tài khoản không thuộc đội giao hàng nào."));
        return team.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryOrderSummaryDTO> getAssignedOrders(Authentication authentication) {
        Integer teamId = getCurrentDeliveryTeamId(authentication);
        List<OrderDelivery.DeliveryStatus> statuses = Arrays.asList(
                OrderDelivery.DeliveryStatus.IN_TRANSIT,
                OrderDelivery.DeliveryStatus.RETURN_PICKUP
        );
        
        // Phương thức này giữ nguyên OrderBy vì dùng List, không phải Pageable
        List<OrderDelivery> deliveries = orderDeliveryRepository
                .findByDeliveryTeamIdAndStatusInOrderByUpdatedAtDesc(teamId, statuses);

        return deliveries.stream()
                .map(DeliveryOrderSummaryDTO::fromEntity)
                .filter(java.util.Objects::nonNull) // Lọc bỏ DTO null có thể xảy ra do mapping
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryOrderDetailDTO getOrderDetail(Authentication authentication, Integer orderDeliveryId) {
        Integer teamId = getCurrentDeliveryTeamId(authentication);
        OrderDelivery orderDelivery = orderDeliveryRepository
                .findByIdAndDeliveryTeamId(orderDeliveryId, teamId) // Tìm theo ID và teamId để bảo mật
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn giao hàng hoặc bạn không có quyền xem."));

        // Map sang DTO chi tiết
        return DeliveryOrderDetailDTO.fromEntity(orderDelivery);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryOrderDetailDTO getOrderDetailByOrderId(Authentication authentication, Integer orderId) {
        Integer teamId = getCurrentDeliveryTeamId(authentication);
        OrderDelivery orderDelivery = orderDeliveryRepository.findByOrderIdAndDeliveryTeamId(orderId, teamId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn giao hàng hoặc bạn không có quyền xem."));

        // Map sang DTO chi tiết
        return DeliveryOrderDetailDTO.fromEntity(orderDelivery);
    }

    @Override
    @Transactional
    public MessageResponse confirmDelivery(Authentication authentication, Integer orderDeliveryId, DeliveryUpdateRequestDTO request) {
        User currentUser = getCurrentUser(authentication);
        Integer teamId = getCurrentDeliveryTeamId(authentication);

        // Kiểm tra xem OrderDelivery có tồn tại và thuộc team này không
        OrderDelivery od = orderDeliveryRepository.findByIdAndDeliveryTeamId(orderDeliveryId, teamId)
                .orElseThrow(() -> new RuntimeException("Đơn giao hàng không hợp lệ hoặc không được phân công cho bạn."));

        // Chỉ xác nhận khi đang IN_TRANSIT
        if (od.getStatus() != OrderDelivery.DeliveryStatus.IN_TRANSIT) {
            return MessageResponse.error("Không thể xác nhận giao hàng cho đơn ở trạng thái " + od.getStatus());
        }
         // Kiểm tra null cho order gốc
        if (od.getOrder() == null) {
             return MessageResponse.error("Lỗi: Không tìm thấy thông tin đơn hàng gốc.");
        }
        Integer orderId = od.getOrder().getId();

        try {
            // Xử lý upload ảnh nếu có
            String proofImageUrl = null;
            if (request.getProofImageFile() != null && !request.getProofImageFile().isEmpty()) {
                try {
                    proofImageUrl = fileUploadService.uploadDeliveryProofImage(request.getProofImageFile(), orderId);
                } catch (Exception e) {
                    return MessageResponse.error("Lỗi upload ảnh: " + e.getMessage());
                }
            } else if (request.getProofImageUrl() != null && !request.getProofImageUrl().trim().isEmpty()) {
                // Fallback cho URL cũ (để tương thích)
                proofImageUrl = request.getProofImageUrl();
                if (!proofImageUrl.startsWith("/static/images/deliveries/")) {
                    proofImageUrl = null;
                }
            }

            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("dbo.sp_MarkDelivered");
            query.registerStoredProcedureParameter("order_id", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("proof_image_url", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("actor_user_id", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("note", String.class, ParameterMode.IN);

            query.setParameter("order_id", orderId);
            query.setParameter("proof_image_url", proofImageUrl);
            query.setParameter("actor_user_id", currentUser.getId()); // ID của người dùng delivery
            query.setParameter("note", request.getNote()); // Có thể null

            query.execute();

            // Kiểm tra output parameters hoặc kết quả trả về của SP nếu có

            return MessageResponse.success("Xác nhận giao hàng thành công cho đơn #" + orderId);
        } catch (Exception e) {
            String rootCauseMessage = getRootCauseMessage(e); // Lấy lỗi gốc
            return MessageResponse.error("Lỗi khi xác nhận giao hàng: " + rootCauseMessage);
        }
    }

    @Override
    @Transactional
    public MessageResponse processReturnPickup(Authentication authentication, Integer orderDeliveryId, DeliveryUpdateRequestDTO request) {
        User currentUser = getCurrentUser(authentication);
        Integer teamId = getCurrentDeliveryTeamId(authentication);

        OrderDelivery od = orderDeliveryRepository.findByIdAndDeliveryTeamId(orderDeliveryId, teamId)
                .orElseThrow(() -> new RuntimeException("Đơn giao hàng không hợp lệ hoặc không được phân công cho bạn."));

        // Chỉ xử lý khi đang RETURN_PICKUP
        if (od.getStatus() != OrderDelivery.DeliveryStatus.RETURN_PICKUP) {
            return MessageResponse.error("Không thể xử lý thu hồi cho đơn ở trạng thái " + od.getStatus());
        }
         // Kiểm tra null cho order gốc
         if (od.getOrder() == null) {
             return MessageResponse.error("Lỗi: Không tìm thấy thông tin đơn hàng gốc.");
        }

        // Kiểm tra refundMethod có hợp lệ không
        if (request.getRefundMethod() == null || request.getRefundMethod().isBlank()) {
             return MessageResponse.error("Vui lòng chọn phương thức hoàn tiền.");
        }
        List<String> allowedMethods = Arrays.asList("COD_CASH", "BANK_TRANSFER", "VNPAY", "MOMO");
        if (!allowedMethods.contains(request.getRefundMethod().toUpperCase())) {
            return MessageResponse.error("Phương thức hoàn tiền không hợp lệ.");
        }

        Integer orderId = od.getOrder().getId();

        try {
            // Xử lý upload ảnh thu hồi nếu có
            String returnProofImageUrl = null;
            if (request.getProofImageFile() != null && !request.getProofImageFile().isEmpty()) {
                try {
                    returnProofImageUrl = fileUploadService.uploadReturnProofImage(request.getProofImageFile(), orderId);
                } catch (Exception e) {
                    return MessageResponse.error("Lỗi upload ảnh thu hồi: " + e.getMessage());
                }
            } else if (request.getProofImageUrl() != null && !request.getProofImageUrl().trim().isEmpty()) {
                // Fallback cho URL cũ (để tương thích)
                returnProofImageUrl = request.getProofImageUrl();
            }

            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("dbo.sp_ReturnOrder");
            query.registerStoredProcedureParameter("order_id", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("actor_user_id", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("reason", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("refund_method", String.class, ParameterMode.IN);

            query.setParameter("order_id", orderId);
            query.setParameter("actor_user_id", currentUser.getId());
            query.setParameter("reason", request.getNote()); // Ghi chú của delivery
            query.setParameter("refund_method", request.getRefundMethod().toUpperCase()); // Chuẩn hóa

            query.execute();

            // Cập nhật OrderDelivery với ảnh thu hồi nếu có
            if (returnProofImageUrl != null) {
                od.setProofImageUrl(returnProofImageUrl);
                od.setNote(request.getNote());
                orderDeliveryRepository.save(od);
            }

            return MessageResponse.success("Xử lý thu hồi và hoàn tiền thành công cho đơn #" + orderId);
        } catch (Exception e) {
             String rootCauseMessage = getRootCauseMessage(e); // Lấy lỗi gốc
            return MessageResponse.error("Lỗi khi xử lý thu hồi hàng: " + rootCauseMessage);
        }
    }

    @Override
    @Transactional
    public MessageResponse confirmDeliveryByOrderId(Authentication authentication, Integer orderId, DeliveryUpdateRequestDTO request) {
        User currentUser = getCurrentUser(authentication);
        Integer teamId = getCurrentDeliveryTeamId(authentication);

        // Tìm OrderDelivery bằng orderId và teamId
        OrderDelivery od = orderDeliveryRepository.findByOrderIdAndDeliveryTeamId(orderId, teamId)
                .orElseThrow(() -> new RuntimeException("Đơn giao hàng không hợp lệ hoặc không được phân công cho bạn."));

        // Chỉ xác nhận khi đang IN_TRANSIT
        if (od.getStatus() != OrderDelivery.DeliveryStatus.IN_TRANSIT) {
            return MessageResponse.error("Không thể xác nhận giao hàng cho đơn ở trạng thái " + od.getStatus());
        }
         // Kiểm tra null cho order gốc
        if (od.getOrder() == null) {
             return MessageResponse.error("Lỗi: Không tìm thấy thông tin đơn hàng gốc.");
        }

        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("dbo.sp_MarkDelivered");
            query.registerStoredProcedureParameter("order_id", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("proof_image_url", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("actor_user_id", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("note", String.class, ParameterMode.IN);

            // Xử lý proof_image_url để tuân thủ constraint
            String proofImageUrl = request.getProofImageUrl();
            if (proofImageUrl != null && !proofImageUrl.trim().isEmpty()) {
                // Nếu có URL nhưng không đúng format, chuyển thành null
                if (!proofImageUrl.startsWith("/static/images/deliveries/")) {
                    proofImageUrl = null;
                }
            } else {
                proofImageUrl = null;
            }

            query.setParameter("order_id", orderId);
            query.setParameter("proof_image_url", proofImageUrl); // Đã xử lý để tuân thủ constraint
            query.setParameter("actor_user_id", currentUser.getId()); // ID của người dùng delivery
            query.setParameter("note", request.getNote()); // Có thể null

            query.execute();

            return MessageResponse.success("Xác nhận giao hàng thành công cho đơn #" + orderId);
        } catch (Exception e) {
            String rootCauseMessage = getRootCauseMessage(e); // Lấy lỗi gốc
            return MessageResponse.error("Lỗi khi xác nhận giao hàng: " + rootCauseMessage);
        }
    }

    @Override
    @Transactional
    public MessageResponse processReturnPickupByOrderId(Authentication authentication, Integer orderId, DeliveryUpdateRequestDTO request) {
        User currentUser = getCurrentUser(authentication);
        Integer teamId = getCurrentDeliveryTeamId(authentication);

        // Tìm OrderDelivery bằng orderId và teamId
        OrderDelivery od = orderDeliveryRepository.findByOrderIdAndDeliveryTeamId(orderId, teamId)
                .orElseThrow(() -> new RuntimeException("Đơn giao hàng không hợp lệ hoặc không được phân công cho bạn."));

        // Chỉ xử lý khi đang RETURN_PICKUP
        if (od.getStatus() != OrderDelivery.DeliveryStatus.RETURN_PICKUP) {
            return MessageResponse.error("Không thể xử lý thu hồi cho đơn ở trạng thái " + od.getStatus());
        }
         // Kiểm tra null cho order gốc
         if (od.getOrder() == null) {
             return MessageResponse.error("Lỗi: Không tìm thấy thông tin đơn hàng gốc.");
        }

        // Kiểm tra refundMethod có hợp lệ không
        if (request.getRefundMethod() == null || request.getRefundMethod().isBlank()) {
             return MessageResponse.error("Vui lòng chọn phương thức hoàn tiền.");
        }
        List<String> allowedMethods = Arrays.asList("COD_CASH", "BANK_TRANSFER", "VNPAY", "MOMO");
        if (!allowedMethods.contains(request.getRefundMethod().toUpperCase())) {
            return MessageResponse.error("Phương thức hoàn tiền không hợp lệ.");
        }

        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("dbo.sp_ReturnOrder");
            query.registerStoredProcedureParameter("order_id", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("actor_user_id", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("reason", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("refund_method", String.class, ParameterMode.IN);

            query.setParameter("order_id", orderId);
            query.setParameter("actor_user_id", currentUser.getId());
            query.setParameter("reason", request.getNote()); // Ghi chú của delivery
            query.setParameter("refund_method", request.getRefundMethod().toUpperCase()); // Chuẩn hóa

            query.execute();

            return MessageResponse.success("Xử lý thu hồi và hoàn tiền thành công cho đơn #" + orderId);
        } catch (Exception e) {
             String rootCauseMessage = getRootCauseMessage(e); // Lấy lỗi gốc
            return MessageResponse.error("Lỗi khi xử lý thu hồi hàng: " + rootCauseMessage);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeliveryOrderSummaryDTO> getCompletedOrders(Authentication authentication, Pageable pageable) { // <<<--- GIỮ LẠI 1 PHIÊN BẢN
        Integer teamId = getCurrentDeliveryTeamId(authentication);
        // SỬ DỤNG TÊN ĐÚNG: findByDeliveryTeamIdAndStatus
        Page<OrderDelivery> deliveryPage = orderDeliveryRepository
                .findByDeliveryTeamIdAndStatus(teamId, OrderDelivery.DeliveryStatus.DONE, pageable); // <<<--- ĐẢM BẢO GỌI ĐÚNG TÊN NÀY

        return deliveryPage.map(DeliveryOrderSummaryDTO::fromEntity);
    }

    // --- Chức năng Quản lý tài khoản ---

    @Override
    @Transactional(readOnly = true)
    public User getCurrentDeliveryProfile(Authentication authentication) {
        return getCurrentUser(authentication); // Lấy user hiện tại
    }

    @Override
    @Transactional
    public MessageResponse updateDeliveryProfile(Authentication authentication, ProfileUpdateRequest request) {
         User currentUser = getCurrentUser(authentication);

        // Kiểm tra email trùng (nếu thay đổi và email đó tồn tại ở user khác)
        if (!currentUser.getEmail().equalsIgnoreCase(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
             return MessageResponse.error("Email đã được sử dụng bởi tài khoản khác.");
        }

        currentUser.setFullName(request.getFullName());
        currentUser.setEmail(request.getEmail());
        currentUser.setGender(request.getGender());
        // Chuyển đổi String dob từ request thành LocalDate nếu cần
        try {
             // Đảm bảo request.getDob() trả về LocalDate hoặc null
             currentUser.setDob(request.getDob());
        } catch(Exception e){
              return MessageResponse.error("Định dạng ngày sinh không hợp lệ.");
        }
        currentUser.setPhone(request.getPhone());

        userRepository.save(currentUser);
        return MessageResponse.success("Cập nhật thông tin thành công.");
    }

    @Override
    @Transactional
    public MessageResponse changeDeliveryPassword(Authentication authentication, PasswordChangeRequest request) {
        User currentUser = getCurrentUser(authentication);

        // 1. Kiểm tra mật khẩu hiện tại có được nhập không
        if (request.getCurrentPassword() == null || request.getCurrentPassword().isEmpty()) {
            return MessageResponse.error("Vui lòng nhập mật khẩu hiện tại.");
        }
        // 2. Kiểm tra mật khẩu hiện tại có đúng không
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
             return MessageResponse.error("Mật khẩu hiện tại không đúng.");
        }
        // 3. Kiểm tra mật khẩu mới có được nhập không
        if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            return MessageResponse.error("Vui lòng nhập mật khẩu mới.");
        }
         // 4. Kiểm tra mật khẩu mới có đủ mạnh không (có thể thêm validation phức tạp hơn)
         if (request.getNewPassword().length() < 6) {
             return MessageResponse.error("Mật khẩu mới phải có ít nhất 6 ký tự.");
         }
        // 5. Kiểm tra mật khẩu mới và xác nhận có khớp không
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
             return MessageResponse.error("Mật khẩu mới và xác nhận mật khẩu không khớp.");
        }

        // Cập nhật mật khẩu mới đã hash
        currentUser.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);

        return MessageResponse.success("Thay đổi mật khẩu thành công.");
    }

    // Helper lấy message lỗi gốc
    private String getRootCauseMessage(Throwable throwable) {
        if (throwable == null) return "Unknown error";
        String message = throwable.getMessage();
        Throwable cause = throwable.getCause();
        while(cause != null) {
            message = cause.getMessage();
            cause = cause.getCause();
        }
        return message != null ? message : "Error executing database operation."; // Trả về thông báo chung nếu message gốc là null
    }
}