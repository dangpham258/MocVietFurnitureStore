package mocviet.service.customer.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.OrderDetailDTO;
import mocviet.dto.OrderItemDTO;
import mocviet.dto.StatusHistoryDTO;
import mocviet.entity.*;
import mocviet.repository.*;
import mocviet.service.UserDetailsServiceImpl;
import mocviet.service.customer.IOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final UserDetailsServiceImpl userDetailsService;
    
    @Override
    @Transactional
    public Page<Orders> getCurrentUserOrders(Pageable pageable) {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            return Page.empty();
        }
        
        // Lấy Page<Orders> không fetch collection để tránh cảnh báo HHH90003004
        Page<Orders> ordersPage = orderRepository.findByUserId(currentUser.getId(), pageable);
        
        if (ordersPage.isEmpty()) {
            return ordersPage;
        }
        
        // Fetch orderItems riêng cho các orders trong page
        List<Integer> orderIds = ordersPage.getContent().stream()
                .map(Orders::getId)
                .toList();
        
        List<Orders> ordersWithItems = orderRepository.findByIdsWithOrderItems(orderIds);
        
        // Map orderItems vào orders
        Map<Integer, Orders> ordersMap = ordersWithItems.stream()
                .collect(Collectors.toMap(Orders::getId, order -> order));
        
        ordersPage.getContent().forEach(order -> {
            Orders orderWithItems = ordersMap.get(order.getId());
            if (orderWithItems != null) {
                // Copy orderItems
                order.setOrderItems(orderWithItems.getOrderItems());
            }
        });
        
        // Fetch productImages riêng cho tất cả products trong orderItems
        List<Integer> productIds = ordersPage.getContent().stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(item -> item.getVariant().getProduct().getId())
                .distinct()
                .toList();
        
        if (!productIds.isEmpty()) {
            List<Product> productsWithImages = productRepository.findByIdsWithImages(productIds);
            Map<Integer, Product> productsMap = productsWithImages.stream()
                    .collect(Collectors.toMap(Product::getId, product -> product));
            
            // Map productImages vào products
            ordersPage.getContent().forEach(order -> {
                order.getOrderItems().forEach(item -> {
                    if (item.getVariant() != null && 
                        item.getVariant().getProduct() != null) {
                        
                        Product productWithImages = productsMap.get(item.getVariant().getProduct().getId());
                        if (productWithImages != null) {
                            item.getVariant().setProduct(productWithImages);
                        }
                    }
                });
            });
        }
        
        return ordersPage;
    }
    
    @Override
    @Transactional
    public List<Orders> getCurrentUserOrdersByStatus(Orders.OrderStatus status) {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }
        return orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(currentUser.getId(), status);
    }
    
    @Override
    @Transactional
    public List<Orders> getCurrentUserOrdersByReturnStatus(Orders.ReturnStatus returnStatus) {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }
        return orderRepository.findByUserIdAndReturnStatusOrderByCreatedAtDesc(currentUser.getId(), returnStatus);
    }
    
    @Override
    @Transactional
    public Orders getOrderDetail(Integer orderId) {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        
        Orders order = orderRepository.findByIdAndUserId(orderId, currentUser.getId()).orElse(null);
        if (order == null) {
            return null;
        }
        
        // Fetch orderItems với ProductVariant và Color
        List<OrderItem> orderItems = orderItemRepository.findByOrderIdWithVariantAndColor(orderId);
        order.setOrderItems(orderItems);
        
        // Fetch Product cho tất cả ProductVariant trong orderItems
        List<Integer> productIds = orderItems.stream()
                .map(item -> item.getVariant().getProduct().getId())
                .distinct()
                .toList();
        
        if (!productIds.isEmpty()) {
            List<Product> productsWithImages = productRepository.findByIdsWithImages(productIds);
            Map<Integer, Product> productsMap = productsWithImages.stream()
                    .collect(Collectors.toMap(Product::getId, product -> product));
            
            // Map Product vào ProductVariant
            orderItems.forEach(item -> {
                if (item.getVariant() != null && 
                    item.getVariant().getProduct() != null) {
                    
                    Product productWithImages = productsMap.get(item.getVariant().getProduct().getId());
                    if (productWithImages != null) {
                        item.getVariant().setProduct(productWithImages);
                    }
                }
            });
        }
        
        return order;
    }
    
    /**
     * Lấy chi tiết đơn hàng dưới dạng DTO để tránh lazy loading
     */
    @Transactional
    public OrderDetailDTO getOrderDetailDTO(Integer orderId) {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        
        Orders order = orderRepository.findByIdAndUserId(orderId, currentUser.getId()).orElse(null);
        if (order == null) {
            return null;
        }
        
        // Fetch orderItems với ProductVariant và Color
        List<OrderItem> orderItems = orderItemRepository.findByOrderIdWithVariantAndColor(orderId);
        
        // Fetch status histories
        List<OrderStatusHistory> statusHistories = orderStatusHistoryRepository.findByOrderIdOrderByChangedAtAsc(orderId);
        
        // Map sang DTO
        return mapToOrderDetailDTO(order, orderItems, statusHistories);
    }
    
    @Override
    public List<OrderItem> getOrderItems(Integer orderId) {
        return orderItemRepository.findByOrderIdOrderByIdAsc(orderId);
    }
    
    @Override
    public boolean canCancelOrder(Integer orderId) {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        return orderRepository.canCancelOrder(orderId, currentUser.getId());
    }
    
    @Override
    @Transactional
    public boolean cancelOrder(Integer orderId, String reason) {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        try {
            Orders order = orderRepository.findByIdAndUserId(orderId, currentUser.getId()).orElse(null);
            if (order == null || order.getStatus() != Orders.OrderStatus.PENDING) {
                return false;
            }
            
            // Cập nhật trạng thái đơn hàng
            order.setStatus(Orders.OrderStatus.CANCELLED);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            
            // Hoàn lại tồn kho
            List<OrderItem> orderItems = orderItemRepository.findByOrderIdOrderByIdAsc(orderId);
            for (OrderItem item : orderItems) {
                ProductVariant variant = item.getVariant();
                variant.setStockQty(variant.getStockQty() + item.getQty());
                productVariantRepository.save(variant);
            }
            
            // Ghi log lịch sử
            OrderStatusHistory history = new OrderStatusHistory();
            history.setOrder(order);
            history.setStatus(Orders.OrderStatus.CANCELLED.name());
            history.setNote(reason != null ? reason : "Hủy đơn hàng bởi khách hàng");
            history.setChangedBy(currentUser);
            history.setChangedAt(LocalDateTime.now());
            orderStatusHistoryRepository.save(history);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean canRequestReturn(Integer orderId) {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return orderRepository.canRequestReturn(orderId, currentUser.getId(), thirtyDaysAgo);
    }
    
    @Override
    @Transactional
    public boolean requestReturn(Integer orderId, String reason) {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        try {
            Orders order = orderRepository.findByIdAndUserId(orderId, currentUser.getId()).orElse(null);
            if (order == null || order.getStatus() != Orders.OrderStatus.DELIVERED) {
                return false;
            }
            
            // Kiểm tra thời hạn 30 ngày
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            if (order.getCreatedAt().isBefore(thirtyDaysAgo)) {
                return false;
            }
            
            // Kiểm tra đã có yêu cầu trả hàng chưa
            if (order.getReturnStatus() != null && 
                (order.getReturnStatus() == Orders.ReturnStatus.REQUESTED || 
                 order.getReturnStatus() == Orders.ReturnStatus.APPROVED)) {
                return false;
            }
            
            // Cập nhật trạng thái trả hàng
            order.setReturnStatus(Orders.ReturnStatus.REQUESTED);
            order.setReturnReason(reason);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            
            // Ghi log lịch sử
            OrderStatusHistory history = new OrderStatusHistory();
            history.setOrder(order);
            history.setStatus(Orders.OrderStatus.DELIVERED.name());
            history.setNote("YÊU CẦU TRẢ HÀNG: " + (reason != null ? reason : ""));
            history.setChangedBy(currentUser);
            history.setChangedAt(LocalDateTime.now());
            orderStatusHistoryRepository.save(history);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public List<Orders> getOrdersCanReview() {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }
        return orderRepository.findOrdersCanReview(currentUser.getId());
    }
    
    @Override
    public List<OrderItem> getUnreviewedOrderItems(Integer orderId) {
        return orderItemRepository.findUnreviewedItemsByOrderId(orderId);
    }
    
    @Override
    public List<Orders> getOrdersCanReorder() {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }
        List<Orders.OrderStatus> statuses = Arrays.asList(
            Orders.OrderStatus.CANCELLED, 
            Orders.OrderStatus.RETURNED
        );
        return orderRepository.findByUserIdAndStatusInOrderByCreatedAtDesc(currentUser.getId(), statuses);
    }
    
    @Override
    @Transactional
    public Map<String, Object> reorderProducts(Integer orderId) {
        User currentUser = userDetailsService.getCurrentUser();
        Map<String, Object> result = new HashMap<>();
        
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "Vui lòng đăng nhập để thực hiện chức năng này");
            return result;
        }
        
        try {
            Orders order = orderRepository.findByIdAndUserId(orderId, currentUser.getId()).orElse(null);
            if (order == null) {
                result.put("success", false);
                result.put("message", "Không tìm thấy đơn hàng");
                return result;
            }
            
            // Lấy hoặc tạo giỏ hàng
            Cart cart = cartRepository.findByUserId(currentUser.getId()).orElse(null);
            if (cart == null) {
                cart = new Cart();
                cart.setUser(currentUser);
                cart.setCreatedAt(LocalDateTime.now());
                cart.setUpdatedAt(LocalDateTime.now());
                cart = cartRepository.save(cart);
            }
            
            List<OrderItem> orderItems = orderItemRepository.findByOrderIdOrderByIdAsc(orderId);
            int addedCount = 0;
            int skippedCount = 0;
            
            for (OrderItem orderItem : orderItems) {
                ProductVariant variant = orderItem.getVariant();
                
                // Kiểm tra sản phẩm còn active và có tồn kho không
                if (!variant.getIsActive() || variant.getStockQty() <= 0) {
                    skippedCount++;
                    continue;
                }
                
                // Kiểm tra đã có trong giỏ hàng chưa
                CartItem existingCartItem = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variant.getId()).orElse(null);
                
                if (existingCartItem != null) {
                    // Cập nhật số lượng
                    int newQty = existingCartItem.getQty() + orderItem.getQty();
                    if (newQty > variant.getStockQty()) {
                        newQty = variant.getStockQty();
                    }
                    existingCartItem.setQty(newQty);
                    cartItemRepository.save(existingCartItem);
                } else {
                    // Thêm mới vào giỏ hàng
                    int qty = Math.min(orderItem.getQty(), variant.getStockQty());
                    CartItem cartItem = new CartItem();
                    cartItem.setCart(cart);
                    cartItem.setVariant(variant);
                    cartItem.setQty(qty);
                    cartItemRepository.save(cartItem);
                }
                addedCount++;
            }
            
            result.put("success", true);
            result.put("addedCount", addedCount);
            result.put("skippedCount", skippedCount);
            result.put("message", String.format("Đã thêm %d sản phẩm vào giỏ hàng", addedCount));
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Có lỗi xảy ra khi thêm sản phẩm vào giỏ hàng");
        }
        
        return result;
    }
    
    @Override
    public Map<String, Long> getOrderCountsByStatus() {
        User currentUser = userDetailsService.getCurrentUser();
        Map<String, Long> counts = new HashMap<>();
        
        if (currentUser == null) {
            return counts;
        }
        
        for (Orders.OrderStatus status : Orders.OrderStatus.values()) {
            long count = orderRepository.countByUserIdAndStatus(currentUser.getId(), status);
            counts.put(status.name(), count);
        }
        
        return counts;
    }
    
    @Override
    public Map<String, Long> getOrderCountsByReturnStatus() {
        User currentUser = userDetailsService.getCurrentUser();
        Map<String, Long> counts = new HashMap<>();
        
        if (currentUser == null) {
            return counts;
        }
        
        for (Orders.ReturnStatus status : Orders.ReturnStatus.values()) {
            long count = orderRepository.countByUserIdAndReturnStatus(currentUser.getId(), status);
            counts.put(status.name(), count);
        }
        
        return counts;
    }
    
    @Override
    public Map<String, Object> calculateOrderTotal(Integer orderId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<OrderItem> orderItems = orderItemRepository.findByOrderIdOrderByIdAsc(orderId);
            
            BigDecimal subtotal = BigDecimal.ZERO;
            for (OrderItem item : orderItems) {
                BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQty()));
                subtotal = subtotal.add(itemTotal);
            }
            
            Orders order = orderRepository.findById(orderId).orElse(null);
            BigDecimal shippingFee = order != null ? order.getShippingFee() : BigDecimal.ZERO;
            BigDecimal total = subtotal.add(shippingFee);
            
            result.put("subtotal", subtotal);
            result.put("shippingFee", shippingFee);
            result.put("total", total);
            result.put("itemCount", orderItems.size());
            
        } catch (Exception e) {
            result.put("error", "Có lỗi xảy ra khi tính toán tổng tiền");
        }
        
        return result;
    }
    
    /**
     * Map OrderItem entity sang OrderItemDTO
     */
    private OrderItemDTO mapToOrderItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setSku(item.getVariant().getSku());
        dto.setColorName(item.getVariant().getColor().getName());
        dto.setTypeName(item.getVariant().getTypeName());
        dto.setQty(item.getQty());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQty())));
        return dto;
    }
    
    /**
     * Map OrderStatusHistory entity sang StatusHistoryDTO
     */
    private StatusHistoryDTO mapToStatusHistoryDTO(OrderStatusHistory history) {
        StatusHistoryDTO dto = new StatusHistoryDTO();
        dto.setId(history.getId());
        dto.setStatus(history.getStatus());
        dto.setNote(history.getNote());
        dto.setChangedAt(history.getChangedAt());
        return dto;
    }
    
    /**
     * Map Orders entity sang OrderDetailDTO
     */
    private OrderDetailDTO mapToOrderDetailDTO(Orders order, List<OrderItem> orderItems, List<OrderStatusHistory> statusHistories) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus().name());
        dto.setReturnStatus(order.getReturnStatus() != null ? order.getReturnStatus().name() : null);
        dto.setReturnReason(order.getReturnReason());
        dto.setReturnNote(order.getReturnNote());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setShippingFee(order.getShippingFee());
        dto.setPaymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null);
        dto.setPaymentStatus(order.getPaymentStatus() != null ? order.getPaymentStatus().name() : null);
        
        // Address info
        if (order.getAddress() != null) {
            dto.setReceiverName(order.getAddress().getReceiverName());
            dto.setPhone(order.getAddress().getPhone());
            dto.setAddressLine(order.getAddress().getAddressLine());
            dto.setDistrict(order.getAddress().getDistrict());
            dto.setCity(order.getAddress().getCity());
        }
        
        // Coupon info
        if (order.getCoupon() != null) {
            dto.setCouponCode(order.getCoupon().getCode());
        }
        
        // Map order items
        List<OrderItemDTO> itemDTOs = orderItems.stream()
                .map(this::mapToOrderItemDTO)
                .collect(Collectors.toList());
        dto.setItems(itemDTOs);
        
        // Map status histories
        List<StatusHistoryDTO> historyDTOs = statusHistories.stream()
                .map(this::mapToStatusHistoryDTO)
                .collect(Collectors.toList());
        dto.setStatusHistories(historyDTOs);
        
        // Calculate totals
        BigDecimal subtotal = itemDTOs.stream()
                .map(OrderItemDTO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setSubtotal(subtotal);
        dto.setTotal(subtotal.add(order.getShippingFee()));
        
        // Action flags
        dto.setCanCancel(canCancelOrder(order.getId()));
        dto.setCanRequestReturn(canRequestReturn(order.getId()));
        dto.setCanReview(false); // TODO: implement logic
        dto.setCanReorder(order.getStatus() == Orders.OrderStatus.CANCELLED || 
                         order.getStatus() == Orders.OrderStatus.RETURNED);
        
        return dto;
    }
}
