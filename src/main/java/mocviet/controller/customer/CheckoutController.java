package mocviet.controller.customer;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mocviet.dto.CheckoutSummaryDTO;
import mocviet.dto.CreateOrderRequest;
import mocviet.dto.CreateOrderResponse;
import mocviet.dto.PaymentRequestDTO;
import mocviet.entity.Address;
import mocviet.entity.CartItem;
import mocviet.entity.ProductImage;
import mocviet.entity.ShippingFee;
import mocviet.repository.AddressRepository;
import mocviet.repository.CouponRepository;
import mocviet.repository.ProvinceZoneRepository;
import mocviet.repository.ShippingFeeRepository;
import mocviet.service.customer.IPaymentService;
import mocviet.service.UserDetailsServiceImpl;
import mocviet.service.customer.ICartService;
import mocviet.service.customer.IOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/customer/checkout")
@RequiredArgsConstructor
public class CheckoutController {
    
    private final IOrderService orderService;
    private final ICartService cartService;
    private final AddressRepository addressRepository;
    private final ProvinceZoneRepository provinceZoneRepository;
    private final ShippingFeeRepository shippingFeeRepository;
    private final CouponRepository couponRepository;
    private final mocviet.repository.OrderRepository orderRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final IPaymentService paymentService;
    
    /**
     * Trang checkout - hiển thị form thanh toán
     */
    @GetMapping
    @Transactional(readOnly = true)
    public String checkoutPage(@RequestParam(required = false) String selectedItemIds, Model model) {
        // Lấy cart items được chọn với ProductImages (eager fetch)
        List<CartItem> allCartItems = cartService.getCurrentUserCartItemsWithImages();
        
        if (allCartItems.isEmpty()) {
            return "redirect:/customer/cart?error=empty";
        }
        
        // Parse selected item IDs
        List<Integer> selectedIds = new ArrayList<>();
        if (selectedItemIds != null && !selectedItemIds.isEmpty()) {
            try {
                String[] ids = selectedItemIds.split(",");
                for (String id : ids) {
                    selectedIds.add(Integer.parseInt(id.trim()));
                }
            } catch (Exception e) {
                // Ignore parse error
            }
        }
        
        // Nếu không có item nào được chọn, redirect về cart với lỗi
        if (selectedIds.isEmpty()) {
            return "redirect:/customer/cart?error=not_selected";
        }
        
        List<CheckoutSummaryDTO.CheckoutItemDTO> checkoutItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (CartItem item : allCartItems) {
            // Chỉ thêm nếu được chọn
            boolean isSelected = selectedIds.contains(item.getId());
            
            if (isSelected && item.getVariant() != null && item.getVariant().getIsActive() && item.getVariant().getProduct() != null) {
                CheckoutSummaryDTO.CheckoutItemDTO dto = new CheckoutSummaryDTO.CheckoutItemDTO();
                dto.setCartItemId(item.getId());
                dto.setVariantId(item.getVariant().getId());
                dto.setProductName(item.getVariant().getProduct().getName());
                dto.setProductSlug(item.getVariant().getProduct().getSlug());
                
                if (item.getVariant().getColor() != null) {
                    dto.setColorName(item.getVariant().getColor().getName());
                }
                dto.setTypeName(item.getVariant().getTypeName());
                dto.setUnitPrice(item.getVariant().getSalePrice());
                dto.setQty(item.getQty());
                dto.setTotalPrice(item.getVariant().getSalePrice().multiply(BigDecimal.valueOf(item.getQty())));
                
                // Get first image (already eager fetched)
                String imageUrl = "/images/products/placeholder.jpg";
                if (item.getVariant().getProduct().getProductImages() != null && 
                    !item.getVariant().getProduct().getProductImages().isEmpty()) {
                    imageUrl = item.getVariant().getProduct().getProductImages().get(0).getUrl();
                }
                dto.setImageUrl(imageUrl);
                
                checkoutItems.add(dto);
                subtotal = subtotal.add(dto.getTotalPrice());
            }
        }
        
        if (checkoutItems.isEmpty()) {
            return "redirect:/customer/cart?error=not_found";
        }
        
        // Lấy danh sách địa chỉ của user
        List<Address> addresses = addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(
            userDetailsService.getCurrentUser().getId()
        );
        
        if (addresses.isEmpty()) {
            return "redirect:/customer/profile?error=no_address&goto_address=true";
        }
        
        CheckoutSummaryDTO summary = CheckoutSummaryDTO.builder()
            .items(checkoutItems)
            .subtotal(subtotal)
            .discountAmount(BigDecimal.ZERO)
            .shippingFee(BigDecimal.ZERO) // Sẽ tính sau khi chọn địa chỉ
            .total(subtotal)
            .selectedPaymentMethod("COD")
            .build();
        
        model.addAttribute("summary", summary);
        model.addAttribute("addresses", addresses);
        model.addAttribute("hasDefaultAddress", addresses.stream().anyMatch(Address::getIsDefault));
        
        return "customer/checkout";
    }
    
    /**
     * API tính phí vận chuyển
     */
    @GetMapping("/shipping")
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> calculateShipping(@RequestParam Integer addressId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Lấy địa chỉ
            Address address = addressRepository.findById(addressId).orElse(null);
            if (address == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy địa chỉ");
                return ResponseEntity.ok(response);
            }
            
            // Get zone ID từ province
            Integer zoneId = provinceZoneRepository.findZoneIdByProvinceName(address.getCity()).orElse(null);
            
            if (zoneId == null) {
                // Nếu chưa có dữ liệu, return phí mặc định
                response.put("success", true);
                response.put("shippingFee", BigDecimal.valueOf(30000));
                response.put("message", "Phí vận chuyển mặc định");
                return ResponseEntity.ok(response);
            }
            
            // Get shipping fee
            ShippingFee shippingFee = shippingFeeRepository.findByZoneId(zoneId).orElse(null);
            
            if (shippingFee != null) {
                response.put("success", true);
                response.put("shippingFee", shippingFee.getBaseFee());
                response.put("message", "Phí vận chuyển");
            } else {
                // Default fee
                response.put("success", true);
                response.put("shippingFee", BigDecimal.valueOf(30000));
                response.put("message", "Phí vận chuyển mặc định");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi tính phí vận chuyển: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API validate coupon
     */
    @GetMapping("/coupon/validate")
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> validateCoupon(@RequestParam String code, 
                                                              @RequestParam BigDecimal subtotal) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate coupon với repository
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.util.Optional<mocviet.entity.Coupon> couponOpt = couponRepository.findValidCoupon(code, now, subtotal);
            
            if (couponOpt.isPresent()) {
                mocviet.entity.Coupon coupon = couponOpt.get();
                
                // Kiểm tra user đã dùng coupon này chưa
                Integer userId = userDetailsService.getCurrentUser().getId();
                boolean hasUsed = orderRepository.hasUserUsedCoupon(userId, code);
                
                if (hasUsed) {
                    response.put("success", false);
                    response.put("message", "Bạn đã sử dụng mã giảm giá này rồi. Mỗi mã chỉ được dùng 1 lần.");
                    return ResponseEntity.ok(response);
                }
                
                BigDecimal discountPercent = coupon.getDiscountPercent();
                BigDecimal discountAmount = subtotal.multiply(
                    discountPercent.divide(BigDecimal.valueOf(100))
                ).setScale(0, java.math.RoundingMode.HALF_UP);
                
                // Làm tròn bậc nghìn
                discountAmount = discountAmount.setScale(-3, java.math.RoundingMode.HALF_UP);
                
                response.put("success", true);
                response.put("discountAmount", discountAmount);
                response.put("discountPercent", discountPercent);
                response.put("message", "Áp dụng mã giảm giá thành công");
            } else {
                response.put("success", false);
                response.put("message", "Mã giảm giá không tồn tại hoặc đã hết hạn");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi validate mã giảm giá: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API tạo đơn hàng
     */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request,
                                                          HttpServletRequest httpRequest) {
        try {
            // Validate request
            if (request.getAddressId() == null) {
                throw new RuntimeException("Vui lòng chọn địa chỉ giao hàng");
            }
            
            if (request.getPaymentMethod() == null || request.getPaymentMethod().isEmpty()) {
                throw new RuntimeException("Vui lòng chọn phương thức thanh toán");
            }
            
            if (request.getItems() == null || request.getItems().isEmpty()) {
                throw new RuntimeException("Giỏ hàng không được trống");
            }
            
            CreateOrderResponse response = orderService.createOrder(request);
            
            // Generate payment URL nếu là VNPAY/MoMo
            if ("VNPAY".equals(request.getPaymentMethod()) || "MOMO".equals(request.getPaymentMethod())) {
                try {
                    PaymentRequestDTO paymentRequest = PaymentRequestDTO.builder()
                        .orderId(response.getOrderId())
                        .paymentMethod(request.getPaymentMethod())
                        .amount(response.getGrandTotal())
                        .orderDescription("MocViet Furniture - Order #" + response.getOrderId())
                        .returnUrl(httpRequest.getRequestURL().toString().replace("/create", "/callback"))
                        .ipAddress(getClientIpAddress(httpRequest))
                        .build();
                    
                    String paymentUrl = paymentService.createPaymentUrl(paymentRequest);
                    response.setPaymentUrl(paymentUrl);
                } catch (Exception e) {
                    // Nếu không tạo được payment URL, vẫn cho phép order được tạo với status PENDING
                    response.setPaymentUrl(null);
                    response.setMessage("Đơn hàng đã được tạo nhưng không thể tạo link thanh toán. Vui lòng liên hệ CSKH.");
                }
            }
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Return error response
            CreateOrderResponse errorResponse = CreateOrderResponse.builder()
                .orderId(null)
                .status("ERROR")
                .paymentStatus("ERROR")
                .subtotalSnapshot(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .totalAfterCoupon(BigDecimal.ZERO)
                .shippingFee(BigDecimal.ZERO)
                .grandTotal(BigDecimal.ZERO)
                .message("Lỗi: " + e.getMessage())
                .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Lấy IP address của client
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        // Lấy IP đầu tiên nếu có nhiều IP (vì X-Forwarded-For có thể chứa nhiều IP)
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        return ipAddress;
    }
}

