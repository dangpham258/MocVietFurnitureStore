package mocviet.dto.delivery;

import lombok.Data;
import mocviet.dto.OrderItemDTO;
import mocviet.dto.StatusHistoryDTO;
import mocviet.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator; // <<<--- ĐÃ THÊM IMPORT
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        dto.setCustomerPhone(address != null ? address.getPhone() : "N/A");

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
                        // Cần kiểm tra null trước khi nhân
                        (item.getUnitPrice() != null && item.getQty() != null) ? item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQty())) : BigDecimal.ZERO,
                        (item.getVariant() != null && item.getVariant().getProduct() != null) ? item.getVariant().getProduct().getSlug() : null
                ))
                .collect(Collectors.toList()));

            // Tính tổng tiền
            dto.setTotalAmount(order.getOrderItems().stream()
                // Kiểm tra null unitPrice và qty
                .map(item -> (item.getUnitPrice() != null && item.getQty() != null) ? item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQty())) : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO));
        } else {
            dto.setItems(List.of());
            dto.setTotalAmount(order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO);
        }

        // History
        if (order.getStatusHistories() != null) {
            dto.setOrderStatusHistory(order.getStatusHistories().stream()
                .map(h -> new StatusHistoryDTO(h.getId(), h.getStatus(), h.getNote(), h.getChangedAt()))
                .sorted(Comparator.comparing(StatusHistoryDTO::getChangedAt).reversed()) // <<<--- Sử dụng Comparator
                .collect(Collectors.toList()));
        } else {
            dto.setOrderStatusHistory(List.of());
        }

        if (od.getDeliveryHistories() != null) {
            dto.setDeliveryHistory(od.getDeliveryHistories().stream()
                .map(h -> new StatusHistoryDTO(h.getId(), h.getStatus() != null ? h.getStatus().name() : null, h.getNote(), h.getChangedAt())) // Kiểm tra null status
                .sorted(Comparator.comparing(StatusHistoryDTO::getChangedAt).reversed()) // <<<--- Sử dụng Comparator
                .collect(Collectors.toList()));
        } else {
            dto.setDeliveryHistory(List.of());
        }

        // Actions allowed
        dto.setCanConfirmDelivery(od.getStatus() == OrderDelivery.DeliveryStatus.IN_TRANSIT);
        dto.setCanProcessReturn(od.getStatus() == OrderDelivery.DeliveryStatus.RETURN_PICKUP);

        return dto;
    }
}