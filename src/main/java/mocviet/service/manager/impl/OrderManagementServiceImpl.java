package mocviet.service.manager.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.manager.*;
import mocviet.entity.*;
import mocviet.repository.*;
import mocviet.service.manager.IOrderManagementService;
import mocviet.service.manager.IOrderStoredProcedureService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderManagementServiceImpl implements IOrderManagementService {

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final IOrderStoredProcedureService storedProcedureService;

    @Override
    @Transactional(readOnly = true)
    public Page<OrderListDTO> getPendingOrders(Pageable pageable) {
        boolean isSortByTotalAmount = pageable.getSort().stream().anyMatch(order -> order.getProperty().equals("totalAmount"));
        Page<Orders> orders;
        if (isSortByTotalAmount) {
            java.util.List<Orders> allOrders = ordersRepository.findByStatus(Orders.OrderStatus.PENDING);
            boolean isDescending = pageable.getSort().stream().filter(order -> order.getProperty().equals("totalAmount")).anyMatch(order -> order.getDirection().isDescending());
            allOrders = allOrders.stream().sorted((o1, o2) -> {
                java.math.BigDecimal total1 = calculateTotalAmount(o1);
                java.math.BigDecimal total2 = calculateTotalAmount(o2);
                return isDescending ? total2.compareTo(total1) : total1.compareTo(total2);
            }).collect(java.util.stream.Collectors.toList());
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), allOrders.size());
            java.util.List<Orders> pagedOrders = allOrders.subList(start, end);
            orders = new org.springframework.data.domain.PageImpl<>(pagedOrders, pageable, allOrders.size());
        } else {
            orders = ordersRepository.findByStatus(Orders.OrderStatus.PENDING, pageable);
        }
        return orders.map(this::mapToOrderListDTO);
    }

    private java.math.BigDecimal calculateTotalAmount(Orders order) {
        java.math.BigDecimal subtotal = order.getOrderItems().stream().map(item -> item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQty()))).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        return subtotal.add(order.getShippingFee());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderManagementDTO getOrderDetails(Integer orderId) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        return mapToOrderManagementDTO(order);
    }

    @Override
    @Transactional
    public void confirmOrder(Integer orderId, Integer managerId, String note) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        if (order.getStatus() != Orders.OrderStatus.PENDING) throw new RuntimeException("Chỉ xác nhận được đơn hàng ở trạng thái PENDING");
        storedProcedureService.confirmOrder(orderId, managerId, note);
    }

    @Override
    @Transactional
    public void cancelOrder(Integer orderId, Integer managerId, String reason) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        if (order.getStatus() != Orders.OrderStatus.PENDING) throw new RuntimeException("Chỉ hủy được đơn hàng ở trạng thái PENDING");
        if (reason == null || reason.trim().isEmpty()) throw new RuntimeException("Vui lòng nhập lý do hủy đơn");
        storedProcedureService.cancelOrder(orderId, managerId, reason);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderListDTO> getInDeliveryOrders(Pageable pageable) {
        Page<Orders> orders = ordersRepository.findByStatusIn(java.util.Arrays.asList(Orders.OrderStatus.CONFIRMED, Orders.OrderStatus.DISPATCHED), pageable);
        return orders.map(this::mapToOrderListDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderListDTO> getInDeliveryOrdersWithKeyword(String keyword, Pageable pageable) {
        Page<Orders> orders = ordersRepository.findByStatusInAndKeyword(java.util.Arrays.asList(Orders.OrderStatus.CONFIRMED, Orders.OrderStatus.DISPATCHED), keyword, pageable);
        return orders.map(this::mapToOrderListDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderListDTO> getCancelledOrders(Pageable pageable) {
        Page<Orders> orders = ordersRepository.findByStatus(Orders.OrderStatus.CANCELLED, pageable);
        return orders.map(this::mapToOrderListDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderListDTO> getCancelledOrdersWithKeyword(String keyword, Pageable pageable) {
        Page<Orders> orders = ordersRepository.findByStatusAndKeyword(Orders.OrderStatus.CANCELLED, keyword, pageable);
        return orders.map(this::mapToOrderListDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderListDTO> getCompletedOrders(Pageable pageable) {
        Page<Orders> orders = ordersRepository.findCompletedOrders(Orders.OrderStatus.DELIVERED, Orders.ReturnStatus.REJECTED, pageable);
        return orders.map(this::mapToOrderListDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderListDTO> getCompletedOrdersWithFilters(String keyword, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        boolean isSortByTotalAmount = pageable.getSort().stream().anyMatch(order -> order.getProperty().equals("totalAmount"));
        Page<Orders> orders;
        if (isSortByTotalAmount) {
            java.util.List<Orders> allOrders;
            if (keyword != null && !keyword.trim().isEmpty()) {
                allOrders = ordersRepository.findCompletedOrdersWithKeyword(Orders.OrderStatus.DELIVERED, Orders.ReturnStatus.REJECTED, keyword, org.springframework.data.domain.Pageable.unpaged()).getContent();
            } else if (fromDate != null && toDate != null) {
                allOrders = ordersRepository.findCompletedOrdersWithDateRange(Orders.OrderStatus.DELIVERED, Orders.ReturnStatus.REJECTED, fromDate, toDate, org.springframework.data.domain.Pageable.unpaged()).getContent();
            } else {
                allOrders = ordersRepository.findCompletedOrders(Orders.OrderStatus.DELIVERED, Orders.ReturnStatus.REJECTED, org.springframework.data.domain.Pageable.unpaged()).getContent();
            }
            boolean isDescending = pageable.getSort().stream().filter(order -> order.getProperty().equals("totalAmount")).anyMatch(order -> order.getDirection().isDescending());
            allOrders = allOrders.stream().sorted((o1, o2) -> {
                java.math.BigDecimal total1 = calculateTotalAmount(o1);
                java.math.BigDecimal total2 = calculateTotalAmount(o2);
                return isDescending ? total2.compareTo(total1) : total1.compareTo(total2);
            }).collect(java.util.stream.Collectors.toList());
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), allOrders.size());
            java.util.List<Orders> pagedOrders = allOrders.subList(start, end);
            orders = new org.springframework.data.domain.PageImpl<>(pagedOrders, pageable, allOrders.size());
        } else {
            if (keyword != null && !keyword.trim().isEmpty()) {
                orders = ordersRepository.findCompletedOrdersWithKeyword(Orders.OrderStatus.DELIVERED, Orders.ReturnStatus.REJECTED, keyword, pageable);
            } else if (fromDate != null && toDate != null) {
                orders = ordersRepository.findCompletedOrdersWithDateRange(Orders.OrderStatus.DELIVERED, Orders.ReturnStatus.REJECTED, fromDate, toDate, pageable);
            } else {
                orders = ordersRepository.findCompletedOrders(Orders.OrderStatus.DELIVERED, Orders.ReturnStatus.REJECTED, pageable);
            }
        }
        return orders.map(this::mapToOrderListDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderListDTO> getReturnedOrders(Pageable pageable) {
        Page<Orders> orders = ordersRepository.findReturnedOrders(Orders.OrderStatus.RETURNED, Orders.ReturnStatus.PROCESSED, pageable);
        return orders.map(this::mapToOrderListDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderListDTO> getReturnedOrdersWithKeyword(String keyword, Pageable pageable) {
        Page<Orders> orders = ordersRepository.findReturnedOrdersWithKeyword(Orders.OrderStatus.RETURNED, Orders.ReturnStatus.PROCESSED, keyword, pageable);
        return orders.map(this::mapToOrderListDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReturnRequestDTO> getReturnRequests(Pageable pageable) {
        Page<Orders> orders = ordersRepository.findByReturnStatus(Orders.ReturnStatus.REQUESTED, pageable);
        return orders.map(this::mapToReturnRequestDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ReturnRequestDTO getReturnRequestDetails(Integer orderId) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        if (order.getReturnStatus() != Orders.ReturnStatus.REQUESTED) throw new RuntimeException("Yêu cầu trả hàng không hợp lệ");
        return mapToReturnRequestDTO(order);
    }

    @Override
    @Transactional
    public void approveReturn(Integer orderId, Integer managerId, String note, Integer deliveryTeamId) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        if (order.getReturnStatus() != Orders.ReturnStatus.REQUESTED) throw new RuntimeException("Yêu cầu trả hàng không hợp lệ để duyệt");
        if (order.getStatus() != Orders.OrderStatus.DELIVERED) throw new RuntimeException("Đơn hàng chưa được giao, không thể duyệt trả");
        LocalDateTime deliveredAt = getDeliveredAt(order);
        if (deliveredAt != null && deliveredAt.isBefore(LocalDateTime.now().minusDays(30))) throw new RuntimeException("Quá thời hạn trả hàng (30 ngày)");
        storedProcedureService.approveReturn(orderId, managerId, note, deliveryTeamId);
    }

    @Override
    @Transactional
    public void rejectReturn(Integer orderId, Integer managerId, String reason) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        if (order.getReturnStatus() != Orders.ReturnStatus.REQUESTED) throw new RuntimeException("Yêu cầu trả hàng không hợp lệ để từ chối");
        if (reason == null || reason.trim().isEmpty()) throw new RuntimeException("Vui lòng nhập lý do từ chối");
        storedProcedureService.rejectReturn(orderId, managerId, reason);
    }

    private OrderManagementDTO mapToOrderManagementDTO(Orders order) {
        OrderManagementDTO dto = new OrderManagementDTO();
        dto.setId(order.getId());
        dto.setCustomerName(order.getUser().getFullName());
        dto.setCustomerPhone(order.getUser().getPhone());
        dto.setCustomerEmail(order.getUser().getEmail());
        dto.setDeliveryAddress(order.getAddress().getAddressLine());
        dto.setCity(order.getAddress().getCity());
        dto.setDistrict(order.getAddress().getDistrict());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setReturnStatus(order.getReturnStatus());
        dto.setReturnReason(order.getReturnReason());
        dto.setReturnNote(order.getReturnNote());
        dto.setCouponCode(order.getCoupon() != null ? order.getCoupon().getCode() : null);
        dto.setShippingFee(order.getShippingFee());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        BigDecimal subtotal = orderItems.stream().map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQty()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setSubtotal(subtotal);
        dto.setDiscountAmount(BigDecimal.ZERO);
        dto.setTotalAmount(subtotal.add(order.getShippingFee()));
        dto.setOrderItems(orderItems.stream().map(this::mapToOrderItemDTO).collect(Collectors.toList()));
        dto.setStatusHistories(order.getStatusHistories().stream().map(this::mapToOrderStatusHistoryDTO).collect(Collectors.toList()));
        if (order.getOrderDelivery() != null) {
            OrderDelivery delivery = order.getOrderDelivery();
            dto.setDeliveryTeamName(delivery.getDeliveryTeam().getName());
            dto.setDeliveryTeamPhone(delivery.getDeliveryTeam().getPhone());
            dto.setDeliveryStatus(delivery.getStatus().toString());
            dto.setDeliveryNote(delivery.getNote());
            dto.setProofImageUrl(delivery.getProofImageUrl());
            dto.setDeliveryHistories(delivery.getDeliveryHistories().stream().map(this::mapToDeliveryHistoryDTO).collect(Collectors.toList()));
        }
        return dto;
    }

    private OrderListDTO mapToOrderListDTO(Orders order) {
        OrderListDTO dto = new OrderListDTO();
        dto.setId(order.getId());
        dto.setCustomerName(order.getUser().getFullName());
        dto.setCustomerPhone(order.getUser().getPhone());
        dto.setDeliveryAddress(order.getAddress().getAddressLine());
        dto.setCity(order.getAddress().getCity());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setReturnStatus(order.getReturnStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        BigDecimal subtotal = orderItems.stream().map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQty()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalAmount(subtotal.add(order.getShippingFee()));
        dto.setItemCount(orderItems.size());
        dto.setStatusDisplay(getStatusDisplay(order.getStatus()));
        dto.setPaymentStatusDisplay(getPaymentStatusDisplay(order.getPaymentStatus()));
        dto.setReturnStatusDisplay(getReturnStatusDisplay(order.getReturnStatus()));
        if (order.getOrderDelivery() != null) {
            OrderDelivery delivery = order.getOrderDelivery();
            dto.setDeliveryTeamName(delivery.getDeliveryTeam().getName());
            dto.setDeliveryStatusDisplay(getDeliveryStatusDisplay(delivery.getStatus()));
        }
        return dto;
    }

    private ReturnRequestDTO mapToReturnRequestDTO(Orders order) {
        ReturnRequestDTO dto = new ReturnRequestDTO();
        dto.setOrderId(order.getId());
        dto.setCustomerName(order.getUser().getFullName());
        dto.setCustomerPhone(order.getUser().getPhone());
        dto.setDeliveryAddress(order.getAddress().getAddressLine());
        dto.setCity(order.getAddress().getCity());
        dto.setReturnStatus(order.getReturnStatus());
        dto.setReturnReason(order.getReturnReason());
        dto.setReturnNote(order.getReturnNote());
        LocalDateTime deliveredAt = getDeliveredAt(order);
        dto.setOrderDeliveredAt(deliveredAt);
        dto.setReturnRequestedAt(order.getUpdatedAt());
        if (deliveredAt != null) {
            long days = java.time.Duration.between(deliveredAt, LocalDateTime.now()).toDays();
            dto.setDaysSinceDelivery(days);
            dto.setWithinReturnPeriod(days <= 30);
        }
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        dto.setOrderItems(orderItems.stream().map(this::mapToReturnOrderItemDTO).collect(Collectors.toList()));
        if (order.getOrderDelivery() != null) {
            OrderDelivery delivery = order.getOrderDelivery();
            dto.setDeliveryTeamName(delivery.getDeliveryTeam().getName());
            dto.setDeliveryTeamPhone(delivery.getDeliveryTeam().getPhone());
        }
        return dto;
    }

    private OrderManagementDTO.OrderItemDTO mapToOrderItemDTO(OrderItem item) {
        OrderManagementDTO.OrderItemDTO dto = new OrderManagementDTO.OrderItemDTO();
        dto.setId(item.getId());
        dto.setProductName(item.getVariant().getProduct().getName());
        dto.setProductSlug(item.getVariant().getProduct().getSlug());
        dto.setVariantSku(item.getVariant().getSku());
        dto.setColorName(item.getVariant().getColor().getName());
        dto.setTypeName(item.getVariant().getTypeName());
        dto.setQuantity(item.getQty());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQty())));
        return dto;
    }

    private ReturnRequestDTO.OrderItemDTO mapToReturnOrderItemDTO(OrderItem item) {
        ReturnRequestDTO.OrderItemDTO dto = new ReturnRequestDTO.OrderItemDTO();
        dto.setId(item.getId());
        dto.setProductName(item.getVariant().getProduct().getName());
        dto.setVariantSku(item.getVariant().getSku());
        dto.setColorName(item.getVariant().getColor().getName());
        dto.setTypeName(item.getVariant().getTypeName());
        dto.setQuantity(item.getQty());
        return dto;
    }

    private OrderManagementDTO.OrderStatusHistoryDTO mapToOrderStatusHistoryDTO(OrderStatusHistory history) {
        OrderManagementDTO.OrderStatusHistoryDTO dto = new OrderManagementDTO.OrderStatusHistoryDTO();
        dto.setId(history.getId());
        dto.setStatus(Orders.OrderStatus.valueOf(history.getStatus()));
        dto.setNote(history.getNote());
        dto.setChangedBy(history.getChangedBy() != null ? history.getChangedBy().getFullName() : "Hệ thống");
        dto.setChangedAt(history.getChangedAt());
        return dto;
    }

    private OrderManagementDTO.DeliveryHistoryDTO mapToDeliveryHistoryDTO(DeliveryHistory history) {
        OrderManagementDTO.DeliveryHistoryDTO dto = new OrderManagementDTO.DeliveryHistoryDTO();
        dto.setId(history.getId());
        dto.setStatus(history.getStatus().toString());
        dto.setNote(history.getNote());
        dto.setPhotoUrl(history.getPhotoUrl());
        dto.setChangedAt(history.getChangedAt());
        return dto;
    }

    private LocalDateTime getDeliveredAt(Orders order) {
        return order.getStatusHistories().stream().filter(h -> Orders.OrderStatus.valueOf(h.getStatus()) == Orders.OrderStatus.DELIVERED).map(OrderStatusHistory::getChangedAt).findFirst().orElse(null);
    }

    private String getStatusDisplay(Orders.OrderStatus status) {
        switch (status) {
            case PENDING: return "Chờ xác nhận";
            case CONFIRMED: return "Đã xác nhận";
            case DISPATCHED: return "Đã xuất kho";
            case DELIVERED: return "Đã giao hàng";
            case CANCELLED: return "Đã hủy";
            case RETURNED: return "Đã trả hàng";
            default: return status.toString();
        }
    }

    private String getPaymentStatusDisplay(Orders.PaymentStatus status) {
        if (status == null) return "Chưa xác định";
        switch (status) {
            case UNPAID: return "Chưa thanh toán";
            case PAID: return "Đã thanh toán";
            case REFUNDED: return "Đã hoàn tiền";
            default: return status.toString();
        }
    }

    private String getReturnStatusDisplay(Orders.ReturnStatus status) {
        if (status == null) return "Không có";
        switch (status) {
            case REQUESTED: return "Yêu cầu trả hàng";
            case APPROVED: return "Đã duyệt trả";
            case REJECTED: return "Từ chối trả";
            case PROCESSED: return "Đã xử lý trả";
            default: return status.toString();
        }
    }

    private String getDeliveryStatusDisplay(OrderDelivery.DeliveryStatus status) {
        switch (status) {
            case RECEIVED: return "Đã nhận";
            case IN_TRANSIT: return "Đang giao";
            case DONE: return "Hoàn thành";
            case RETURN_PICKUP: return "Thu hồi";
            default: return status.toString();
        }
    }
}


