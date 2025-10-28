package mocviet.controller.customer;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mocviet.dto.PaymentRequestDTO;
import mocviet.dto.PaymentWebhookDTO;
import mocviet.entity.Orders;
import mocviet.repository.OrderRepository;
import mocviet.service.customer.IPaymentService;
import mocviet.service.UserDetailsServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller xử lý thanh toán và webhook từ VNPAY/MoMo
 */
@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    
    private final IPaymentService paymentService;
    private final OrderRepository orderRepository;
    private final UserDetailsServiceImpl userDetailsService;
    
    /**
     * Callback từ VNPAY/MoMo sau khi người dùng thanh toán
     * URL sẽ được redirect về đây
     */
    @GetMapping("/callback")
    public String paymentCallback(@RequestParam Map<String, String> params,
                                  @RequestParam(required = false) String method,
                                  HttpServletRequest request) {
        try {
            PaymentWebhookDTO webhookDto = new PaymentWebhookDTO();
            webhookDto.setPaymentGateway(method);
            
            if ("VNPAY".equalsIgnoreCase(method)) {
                // Parse VNPAY response
                String vnp_ResponseCode = params.get("vnp_ResponseCode");
                String vnp_TxnRef = params.get("vnp_TxnRef");
                String vnp_TransactionNo = params.get("vnp_TransactionNo");
                
                webhookDto.setOrderId(vnp_TxnRef);
                webhookDto.setTransactionCode(vnp_TransactionNo);
                webhookDto.setPaymentStatus("00".equals(vnp_ResponseCode) ? "00" : "fail");
                
            } else if ("MOMO".equalsIgnoreCase(method)) {
                // Parse MoMo response
                String orderId = params.get("orderId");
                String resultCode = params.get("resultCode");
                String transId = params.get("transId");
                
                webhookDto.setOrderId(orderId);
                webhookDto.setTransactionCode(transId);
                webhookDto.setPaymentStatus("0".equals(resultCode) ? "00" : "fail");
            }
            
            // Xử lý webhook
            boolean success = paymentService.handlePaymentWebhook(webhookDto);
            
            // Redirect về trang kết quả thanh toán
            String orderId = webhookDto.getOrderId();
            return "redirect:/payment/result?orderId=" + orderId + "&success=" + success;
            
        } catch (Exception e) {
            return "redirect:/payment/result?success=false&message=Có lỗi xảy ra khi xử lý thanh toán";
        }
    }
    
    /**
     * Trang hiển thị kết quả thanh toán
     */
    @GetMapping("/result")
    @Transactional(readOnly = true)
    public String paymentResult(@RequestParam(required = false) Integer orderId,
                               @RequestParam(required = false, defaultValue = "false") Boolean success,
                               @RequestParam(required = false) String message,
                               Model model) {
        model.addAttribute("success", success);
        model.addAttribute("message", message != null ? message : (success ? "Thanh toán thành công" : "Thanh toán thất bại"));
        
        // Lấy thông tin order nếu có
        if (orderId != null) {
            // Lấy order với eager fetch cho orderItems
            java.util.Optional<Orders> orderOpt = orderRepository.findById(orderId);
            Orders order = orderOpt.orElse(null);
            
            if (order != null) {
                model.addAttribute("order", order);
                
                // Tính tổng tiền đơn hàng (từ order items + shipping fee)
                java.math.BigDecimal grandTotal = java.math.BigDecimal.ZERO;
                try {
                    if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                        for (mocviet.entity.OrderItem item : order.getOrderItems()) {
                            grandTotal = grandTotal.add(item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQty())));
                        }
                    }
                    grandTotal = grandTotal.add(order.getShippingFee());
                } catch (Exception e) {
                    // Nếu không load được orderItems (lazy), skip tính grandTotal
                    grandTotal = null;
                }
                model.addAttribute("grandTotal", grandTotal);
            }
        }
        
        return "customer/payment-result";
    }
    
    /**
     * IPN (Instant Payment Notification) endpoint cho payment gateway
     * Gọi qua POST từ server của VNPAY/MoMo
     */
    @PostMapping("/ipn")
    public ResponseEntity<Map<String, Object>> handleIPN(@RequestParam Map<String, String> params,
                                                          @RequestParam String gateway) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            PaymentWebhookDTO webhookDto = new PaymentWebhookDTO();
            webhookDto.setPaymentGateway(gateway);
            
            if ("VNPAY".equalsIgnoreCase(gateway)) {
                String vnp_ResponseCode = params.get("vnp_ResponseCode");
                String vnp_TxnRef = params.get("vnp_TxnRef");
                String vnp_TransactionNo = params.get("vnp_TransactionNo");
                
                webhookDto.setOrderId(vnp_TxnRef);
                webhookDto.setTransactionCode(vnp_TransactionNo);
                webhookDto.setPaymentStatus("00".equals(vnp_ResponseCode) ? "00" : "fail");
                
            } else if ("MOMO".equalsIgnoreCase(gateway)) {
                String orderId = params.get("orderId");
                String resultCode = params.get("resultCode");
                String transId = params.get("transId");
                
                webhookDto.setOrderId(orderId);
                webhookDto.setTransactionCode(transId);
                webhookDto.setPaymentStatus("0".equals(resultCode) ? "00" : "fail");
            }
            
            boolean success = paymentService.handlePaymentWebhook(webhookDto);
            
            if (success) {
                response.put("RspCode", "00");
                response.put("Message", "Success");
            } else {
                response.put("RspCode", "97");
                response.put("Message", "Fail");
            }
            
        } catch (Exception e) {
            response.put("RspCode", "99");
            response.put("Message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retry payment cho đơn hàng đã tạo
     * Redirect về VNPAY/MoMo để thanh toán
     */
    @GetMapping("/retry/{orderId}")
    @Transactional(readOnly = true)
    public String retryPayment(@PathVariable Integer orderId, 
                               HttpServletRequest request) {
        try {
            // Lấy thông tin user và order
            var currentUser = userDetailsService.getCurrentUser();
            if (currentUser == null) {
                return "redirect:/customer/orders?error=Unauthorized";
            }
            
            // Dùng query với @EntityGraph để eager fetch orderItems
            Orders order = orderRepository.findByIdAndUserId(orderId, currentUser.getId()).orElse(null);
            if (order == null) {
                return "redirect:/customer/orders?error=Order not found";
            }
            
            // Kiểm tra payment status phải là UNPAID
            if (order.getPaymentStatus() != Orders.PaymentStatus.UNPAID) {
                return "redirect:/customer/orders?error=Cannot pay for this order";
            }
            
            // Kiểm tra payment method phải là VNPAY hoặc MoMo
            if (order.getPaymentMethod() != Orders.PaymentMethod.VNPAY && 
                order.getPaymentMethod() != Orders.PaymentMethod.MOMO) {
                return "redirect:/customer/orders?error=Invalid payment method";
            }
            
            // Tính tổng tiền đơn hàng (orderItems đã được eager fetch)
            BigDecimal grandTotal = BigDecimal.ZERO;
            if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                for (var item : order.getOrderItems()) {
                    grandTotal = grandTotal.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQty())));
                }
            }
            grandTotal = grandTotal.add(order.getShippingFee());
            
            // Tạo PaymentRequestDTO
            PaymentRequestDTO paymentRequest = PaymentRequestDTO.builder()
                .orderId(order.getId())
                .paymentMethod(order.getPaymentMethod().name())
                .amount(grandTotal)
                .orderDescription("MocViet Furniture - Order #" + order.getId())
                .returnUrl(request.getScheme() + "://" + request.getServerName() + 
                          (request.getServerPort() != 80 && request.getServerPort() != 443 ? ":" + request.getServerPort() : "") +
                          "/payment/callback?method=" + order.getPaymentMethod().name())
                .ipAddress(getClientIpAddress(request))
                .build();
            
            // Tạo payment URL
            String paymentUrl = paymentService.createPaymentUrl(paymentRequest);
            
            // Redirect đến payment gateway
            return "redirect:" + paymentUrl;
            
        } catch (Exception e) {
            return "redirect:/customer/orders?error=" + e.getMessage();
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
        // Lấy IP đầu tiên nếu có nhiều IP
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        return ipAddress;
    }
}

