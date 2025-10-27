package mocviet.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mocviet.entity.Address; // Import Address
import mocviet.entity.OrderDelivery;
import mocviet.entity.Orders;
import mocviet.entity.User; // Import User

import java.time.LocalDateTime;
import java.util.stream.Collectors; // Import Collectors
import java.util.stream.Stream; // Import Stream

// DTO tóm tắt đơn hàng cho trang danh sách của Delivery
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryOrderSummaryDTO {
    private Integer orderDeliveryId;
    private Integer orderId;
    private String customerName; // Lấy từ order.user.fullName
    private String customerPhone; // Lấy từ order.address.phone
    private String deliveryAddress; // Ghép từ addressLine, district, city
    private Orders.OrderStatus orderStatus; // Trạng thái gốc của Order
    private OrderDelivery.DeliveryStatus deliveryStatus; // Trạng thái giao hàng
    private LocalDateTime updatedAt; // Ngày cập nhật của OrderDelivery
    private int itemCount; // Số lượng loại sản phẩm

    public static DeliveryOrderSummaryDTO fromEntity(OrderDelivery od) {
        if (od == null || od.getOrder() == null) {
            return null; // Hoặc ném lỗi nếu cần
        }
        Orders order = od.getOrder();
        Address address = order.getAddress(); // Thông tin địa chỉ
        User customer = order.getUser();    // Thông tin khách hàng

        String fullAddress = "N/A";
        String phone = "N/A";
        if (address != null) {
            // Ghép địa chỉ, bỏ qua phần tử null hoặc rỗng
            fullAddress = Stream.of(address.getAddressLine(), address.getDistrict(), address.getCity())
                              .filter(s -> s != null && !s.trim().isEmpty())
                              .collect(Collectors.joining(", "));
            phone = address.getPhone();
        }

        String custName = (customer != null) ? customer.getFullName() : "Khách hàng ẩn danh";

        // Đếm số lượng item (cần kiểm tra null)
        int count = (order.getOrderItems() != null) ? order.getOrderItems().size() : 0;

        return new DeliveryOrderSummaryDTO(
                od.getId(),
                order.getId(),
                custName,
                phone,
                fullAddress,
                order.getStatus(),
                od.getStatus(),
                od.getUpdatedAt(),
                count
        );
    }
}