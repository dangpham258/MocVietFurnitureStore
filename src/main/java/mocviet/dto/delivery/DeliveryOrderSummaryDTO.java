package mocviet.dto.delivery;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor; // Import Address
import lombok.Data;
import lombok.NoArgsConstructor;
import mocviet.entity.Address; // Import User
import mocviet.entity.OrderDelivery;
import mocviet.entity.Orders; // Import Collectors
import mocviet.entity.User; // Import Stream

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
            if (fullAddress.isEmpty()) {
                fullAddress = "N/A";
            }
            phone = address.getPhone() != null ? address.getPhone() : "N/A";
        }

        String custName = (customer != null && customer.getFullName() != null) ? customer.getFullName() : "Khách hàng ẩn danh";

        // Đếm số lượng item (cần kiểm tra null)
        int count = 0;
        if (order.getOrderItems() != null) {
            count = order.getOrderItems().size();
        }

        return new DeliveryOrderSummaryDTO(
                od.getId(),
                order.getId(),
                custName,
                phone,
                fullAddress,
                order.getStatus() != null ? order.getStatus() : Orders.OrderStatus.PENDING,
                od.getStatus() != null ? od.getStatus() : OrderDelivery.DeliveryStatus.RECEIVED,
                od.getUpdatedAt() != null ? od.getUpdatedAt() : LocalDateTime.now(),
                count
        );
    }
}