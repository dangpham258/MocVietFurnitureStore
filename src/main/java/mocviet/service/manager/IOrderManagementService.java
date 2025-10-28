package mocviet.service.manager;

import mocviet.dto.manager.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface IOrderManagementService {
    Page<OrderListDTO> getPendingOrders(Pageable pageable);
    OrderManagementDTO getOrderDetails(Integer orderId);
    void confirmOrder(Integer orderId, Integer managerId, String note);
    void cancelOrder(Integer orderId, Integer managerId, String reason);
    Page<OrderListDTO> getInDeliveryOrders(Pageable pageable);
    Page<OrderListDTO> getInDeliveryOrdersWithKeyword(String keyword, Pageable pageable);
    Page<OrderListDTO> getCancelledOrders(Pageable pageable);
    Page<OrderListDTO> getCancelledOrdersWithKeyword(String keyword, Pageable pageable);
    Page<OrderListDTO> getCompletedOrders(Pageable pageable);
    Page<OrderListDTO> getCompletedOrdersWithFilters(String keyword, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
    Page<OrderListDTO> getReturnedOrders(Pageable pageable);
    Page<OrderListDTO> getReturnedOrdersWithKeyword(String keyword, Pageable pageable);
    Page<ReturnRequestDTO> getReturnRequests(Pageable pageable);
    ReturnRequestDTO getReturnRequestDetails(Integer orderId);
    void approveReturn(Integer orderId, Integer managerId, String note, Integer deliveryTeamId);
    void rejectReturn(Integer orderId, Integer managerId, String reason);
}


