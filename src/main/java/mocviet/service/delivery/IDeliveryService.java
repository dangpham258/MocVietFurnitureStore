package mocviet.service.delivery;

import java.util.List; // Thêm import

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Import DTO chung
import org.springframework.security.core.Authentication;   // Import DTO chung

import mocviet.dto.MessageResponse;
import mocviet.dto.PasswordChangeRequest;
import mocviet.dto.ProfileUpdateRequest;
import mocviet.dto.delivery.DeliveryOrderDetailDTO; // Import User
import mocviet.dto.delivery.DeliveryOrderSummaryDTO;
import mocviet.dto.delivery.DeliveryStatsDTO;
import mocviet.dto.delivery.DeliveryUpdateRequestDTO; // Import Authentication
import mocviet.entity.User;

public interface IDeliveryService {

	DeliveryStatsDTO getDeliveryStats(Authentication authentication);
	
    /** Lấy thông tin DeliveryTeam của user hiện tại */
    Integer getCurrentDeliveryTeamId(Authentication authentication);

    /** Lấy danh sách đơn hàng đang chờ xử lý (IN_TRANSIT, RETURN_PICKUP) */
    List<DeliveryOrderSummaryDTO> getAssignedOrders(Authentication authentication);

    /** Lấy chi tiết một đơn hàng được giao */
    DeliveryOrderDetailDTO getOrderDetail(Authentication authentication, Integer orderDeliveryId);

    /** Lấy chi tiết một đơn hàng được giao bằng orderId */
    DeliveryOrderDetailDTO getOrderDetailByOrderId(Authentication authentication, Integer orderId);

    /** Xác nhận đã giao hàng thành công */
    MessageResponse confirmDelivery(Authentication authentication, Integer orderDeliveryId, DeliveryUpdateRequestDTO request);

    /** Xác nhận đã giao hàng thành công bằng orderId */
    MessageResponse confirmDeliveryByOrderId(Authentication authentication, Integer orderId, DeliveryUpdateRequestDTO request);

    /** Xử lý thu hồi hàng và hoàn tiền */
    MessageResponse processReturnPickup(Authentication authentication, Integer orderDeliveryId, DeliveryUpdateRequestDTO request);

    /** Xử lý thu hồi hàng và hoàn tiền bằng orderId */
    MessageResponse processReturnPickupByOrderId(Authentication authentication, Integer orderId, DeliveryUpdateRequestDTO request);

    /** Lấy lịch sử đơn hàng đã hoàn thành (DONE) */
    Page<DeliveryOrderSummaryDTO> getCompletedOrders(Authentication authentication, Pageable pageable);

    /** Lấy thông tin profile của delivery user hiện tại */
    User getCurrentDeliveryProfile(Authentication authentication); // Tương tự customer

    /** Cập nhật thông tin profile */
    MessageResponse updateDeliveryProfile(Authentication authentication, ProfileUpdateRequest request); // Tương tự customer

    /** Thay đổi mật khẩu */
    MessageResponse changeDeliveryPassword(Authentication authentication, PasswordChangeRequest request); // Tương tự customer
}