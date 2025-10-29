package mocviet.dto.delivery;

import lombok.Data;
import mocviet.dto.OrderItemDTO;
import mocviet.dto.StatusHistoryDTO; // Đảm bảo import DTO đã sửa
import mocviet.entity.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.math.BigDecimal; // Đảm bảo import BigDecimal
import java.time.LocalDateTime; // Đảm bảo import LocalDateTime
import java.util.Collections; // <<<--- THÊM IMPORT NÀY

@Data
public class DeliveryOrderDetailDTO {
    // OrderDelivery Info
    private Integer orderDeliveryId;
    private OrderDelivery.DeliveryStatus deliveryStatus;
    private String deliveryNote;
    private String proofImageUrl;
    private LocalDateTime deliveryUpdatedAt;

    // Order Info
    private Integer orderId;
    private Orders.OrderStatus orderStatus;
    private LocalDateTime orderCreatedAt;
    private String paymentMethod;
    private String orderNote; // Giả sử có trường note trong Orders

    // Customer Info
    private String customerName;
    private String customerPhone;

    // Address Info
    private String receiverName;
    private String deliveryAddress;
    private String mapEmbed;

    // Items
    private List<OrderItemDTO> items;
    private BigDecimal totalAmount;

    // History
    private List<StatusHistoryDTO> orderStatusHistory;
    private List<StatusHistoryDTO> deliveryHistory;

    // Actions allowed
    private boolean canConfirmDelivery;
    private boolean canProcessReturn;

    // Factory method
    public static DeliveryOrderDetailDTO fromEntity(OrderDelivery od) {
        if (od == null || od.getOrder() == null) return null;

        DeliveryOrderDetailDTO dto = new DeliveryOrderDetailDTO();
        Orders order = od.getOrder();
        Address address = order.getAddress();
        User customer = order.getUser();

        // OrderDelivery Info
        dto.setOrderDeliveryId(od.getId());
        dto.setDeliveryStatus(od.getStatus());
        dto.setDeliveryNote(od.getNote());
        dto.setProofImageUrl(od.getProofImageUrl());
        dto.setDeliveryUpdatedAt(od.getUpdatedAt());

        // Order Info
        dto.setOrderId(order.getId());
        dto.setOrderStatus(order.getStatus());
        dto.setOrderCreatedAt(order.getCreatedAt());
        dto.setPaymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().name() : "N/A");
        // dto.setOrderNote(order.getNote()); // Lấy note từ Order nếu có

        // Customer Info
        dto.setCustomerName(customer != null ? customer.getFullName() : "N/A");
        dto.setCustomerPhone(address != null ? address.getPhone() : "N/A"); // Lấy SĐT từ Address

        // Address Info
        dto.setReceiverName(address != null ? address.getReceiverName() : "N/A");
        if (address != null) {
            dto.setDeliveryAddress(Stream.of(address.getAddressLine(), address.getDistrict(), address.getCity())
                                      .filter(s -> s != null && !s.trim().isEmpty())
                                      .collect(Collectors.joining(", ")));
        } else {
            dto.setDeliveryAddress("N/A");
        }
        // dto.setMapEmbed(...); // Cần logic lấy map embed

        // Items
        if (order.getOrderItems() != null) {
            dto.setItems(order.getOrderItems().stream()
                .map(item -> new OrderItemDTO(
                        item.getId(),
                        item.getVariant() != null ? item.getVariant().getSku() : "N/A",
                        (item.getVariant() != null && item.getVariant().getColor() != null) ? item.getVariant().getColor().getName() : "N/A",
                        item.getVariant() != null ? item.getVariant().getTypeName() : "N/A",
                        item.getQty(),
                        item.getUnitPrice(),
                        (item.getUnitPrice() != null && item.getQty() != null) ? item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQty())) : BigDecimal.ZERO,
                        (item.getVariant() != null && item.getVariant().getProduct() != null) ? item.getVariant().getProduct().getSlug() : null
                ))
                .collect(Collectors.toList()));

            dto.setTotalAmount(order.getOrderItems().stream()
                .map(item -> (item.getUnitPrice() != null && item.getQty() != null) ? item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQty())) : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO));
        } else {
            dto.setItems(Collections.emptyList()); // <<<--- Dòng 127 dùng Collections
            dto.setTotalAmount(order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO);
        }

        // History
        if (order.getStatusHistories() != null) {
            dto.setOrderStatusHistory(order.getStatusHistories().stream()
                .map(h -> {
                    String changedByName = (h.getChangedBy() != null) ? h.getChangedBy().getFullName() : "Hệ thống";
                    return new StatusHistoryDTO(h.getId(), h.getStatus(), h.getNote(), h.getChangedAt(), changedByName);
                })
                .sorted(Comparator.comparing(StatusHistoryDTO::getChangedAt).reversed())
                .collect(Collectors.toList()));
        } else {
            dto.setOrderStatusHistory(Collections.emptyList()); // <<<--- Dòng 137 dùng Collections
        }

        if (od.getDeliveryHistories() != null) {
            dto.setDeliveryHistory(od.getDeliveryHistories().stream()
                .map(h -> new StatusHistoryDTO(h.getId(), h.getStatus() != null ? h.getStatus().name() : null, h.getNote(), h.getChangedAt(), null))
                .sorted(Comparator.comparing(StatusHistoryDTO::getChangedAt).reversed())
                .collect(Collectors.toList()));
        } else {
            dto.setDeliveryHistory(Collections.emptyList()); // Sử dụng Collections.emptyList()
        }

        // Actions allowed
        dto.setCanConfirmDelivery(od.getStatus() == OrderDelivery.DeliveryStatus.IN_TRANSIT);
        dto.setCanProcessReturn(od.getStatus() == OrderDelivery.DeliveryStatus.RETURN_PICKUP);

        return dto;
    }
}