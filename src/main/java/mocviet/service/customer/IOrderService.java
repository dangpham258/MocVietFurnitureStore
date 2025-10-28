package mocviet.service.customer;

import mocviet.dto.customer.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IOrderService {
    
    Page<OrderSummaryDTO> getCurrentUserOrders(Pageable pageable);
    
    List<OrderSummaryDTO> getCurrentUserOrdersByStatus(String status);
    
    List<OrderSummaryDTO> getCurrentUserOrdersByReturnStatus(String returnStatus);
    
    OrderDetailDTO getOrderDetail(Integer orderId);
    
    List<OrderItemDTO> getOrderItems(Integer orderId);
    
    boolean canCancelOrder(Integer orderId);
    
    boolean cancelOrder(Integer orderId, String reason);
    
    boolean canRequestReturn(Integer orderId);
    
    boolean requestReturn(Integer orderId, String reason);
    
    List<OrderSummaryDTO> getOrdersCanReview();
    
    List<OrderItemDTO> getUnreviewedOrderItems(Integer orderId);
    
    List<OrderSummaryDTO> getOrdersCanReorder();
    
    Map<String, Object> reorderProducts(Integer orderId);
    
    Map<String, Long> getOrderCountsByStatus();
    
    Map<String, Long> getOrderCountsByReturnStatus();
    
    Map<String, Object> calculateOrderTotal(Integer orderId);
    
    CreateOrderResponse createOrder(CreateOrderRequest request);
}
