package mocviet.service.delivery;

import mocviet.dto.delivery.DeliveryStatsDTO; // Thêm import
import mocviet.dto.MessageResponse;
import mocviet.dto.PasswordChangeRequest; // Import DTO chung
import mocviet.dto.ProfileUpdateRequest;   // Import DTO chung
import mocviet.dto.delivery.DeliveryOrderDetailDTO;
import mocviet.dto.delivery.DeliveryOrderSummaryDTO;
import mocviet.dto.delivery.DeliveryUpdateRequestDTO;
import mocviet.entity.User; // Import User
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication; // Import Authentication

import java.util.List;

public interface IDeliveryService {

	DeliveryStatsDTO getDeliveryStats(Authentication authentication);
	
    /** Lấy thông tin DeliveryTeam của user hiện tại */
    Integer getCurrentDeliveryTeamId(Authentication authentication);

    /** Lấy danh sách đơn hàng đang chờ xử lý (IN_TRANSIT, RETURN_PICKUP) */
    List<DeliveryOrderSummaryDTO> getAssignedOrders(Authentication authentication);

    /** Lấy chi tiết một đơn hàng được giao */
    DeliveryOrderDetailDTO getOrderDetail(Authentication authentication, Integer orderDeliveryId);

    /** Xác nhận đã giao hàng thành công */
    MessageResponse confirmDelivery(Authentication authentication, Integer orderDeliveryId, DeliveryUpdateRequestDTO request);

    /** Xử lý thu hồi hàng và hoàn tiền */
    MessageResponse processReturnPickup(Authentication authentication, Integer orderDeliveryId, DeliveryUpdateRequestDTO request);

    /** Lấy lịch sử đơn hàng đã hoàn thành (DONE) */
    Page<DeliveryOrderSummaryDTO> getCompletedOrders(Authentication authentication, Pageable pageable);

    /** Lấy thông tin profile của delivery user hiện tại */
    User getCurrentDeliveryProfile(Authentication authentication); // Tương tự customer

    /** Cập nhật thông tin profile */
    MessageResponse updateDeliveryProfile(Authentication authentication, ProfileUpdateRequest request); // Tương tự customer

    /** Thay đổi mật khẩu */
    MessageResponse changeDeliveryPassword(Authentication authentication, PasswordChangeRequest request); // Tương tự customer
}