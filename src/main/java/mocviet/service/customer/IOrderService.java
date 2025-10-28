package mocviet.service.customer;

import mocviet.dto.CreateOrderRequest;
import mocviet.dto.CreateOrderResponse;
import mocviet.entity.Orders;
import mocviet.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IOrderService {
    
    /**
     * Lấy danh sách đơn hàng của user hiện tại với phân trang
     */
    Page<Orders> getCurrentUserOrders(Pageable pageable);
    
    /**
     * Lấy danh sách đơn hàng theo trạng thái của user hiện tại
     */
    List<Orders> getCurrentUserOrdersByStatus(Orders.OrderStatus status);
    
    /**
     * Lấy danh sách đơn hàng theo return status của user hiện tại
     */
    List<Orders> getCurrentUserOrdersByReturnStatus(Orders.ReturnStatus returnStatus);
    
    /**
     * Lấy chi tiết đơn hàng theo ID (chỉ của user hiện tại)
     */
    Orders getOrderDetail(Integer orderId);
    
    /**
     * Lấy danh sách OrderItem của đơn hàng
     */
    List<OrderItem> getOrderItems(Integer orderId);
    
    /**
     * Kiểm tra đơn hàng có thể hủy không
     */
    boolean canCancelOrder(Integer orderId);
    
    /**
     * Hủy đơn hàng
     */
    boolean cancelOrder(Integer orderId, String reason);
    
    /**
     * Kiểm tra đơn hàng có thể yêu cầu trả không
     */
    boolean canRequestReturn(Integer orderId);
    
    /**
     * Yêu cầu trả hàng
     */
    boolean requestReturn(Integer orderId, String reason);
    
    /**
     * Lấy danh sách đơn hàng có thể đánh giá
     */
    List<Orders> getOrdersCanReview();
    
    /**
     * Lấy danh sách OrderItem chưa đánh giá trong đơn hàng
     */
    List<OrderItem> getUnreviewedOrderItems(Integer orderId);
    
    /**
     * Lấy danh sách đơn hàng có thể mua lại
     */
    List<Orders> getOrdersCanReorder();
    
    /**
     * Mua lại đơn hàng (thêm sản phẩm vào giỏ hàng)
     */
    Map<String, Object> reorderProducts(Integer orderId);
    
    /**
     * Đếm số đơn hàng theo trạng thái
     */
    Map<String, Long> getOrderCountsByStatus();
    
    /**
     * Đếm số đơn hàng theo return status
     */
    Map<String, Long> getOrderCountsByReturnStatus();
    
    /**
     * Tính tổng tiền đơn hàng (từ OrderItems)
     */
    Map<String, Object> calculateOrderTotal(Integer orderId);
    
    /**
     * Tạo đơn hàng mới từ giỏ hàng
     */
    CreateOrderResponse createOrder(CreateOrderRequest request);
}
