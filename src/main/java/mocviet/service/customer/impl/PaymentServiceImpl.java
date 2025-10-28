package mocviet.service.customer.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.PaymentRequestDTO;
import mocviet.dto.PaymentWebhookDTO;
import mocviet.entity.Orders;
import mocviet.repository.OrderRepository;
import mocviet.repository.OrderStatusHistoryRepository;
import mocviet.service.customer.IPaymentService;
import mocviet.service.StoredProcedureService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Implementation của Payment Service
 * Hỗ trợ VNPAY và MoMo payment gateway
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {
    
    @Value("${payment.vnpay.tmnCode:}")
    private String vnpayTmnCode;
    
    @Value("${payment.vnpay.secretKey:}")
    private String vnpaySecretKey;
    
    @Value("${payment.vnpay.url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String vnpayUrl;
    
    @Value("${payment.momo.partnerCode:}")
    private String momoPartnerCode;
    
    @Value("${payment.momo.accessKey:}")
    private String momoAccessKey;
    
    @Value("${payment.momo.secretKey:}")
    private String momoSecretKey;
    
    @Value("${payment.momo.url:https://test-payment.momo.vn/v2/gateway/api/create}")
    private String momoUrl;
    
    @Value("${app.url:http://localhost:8080}")
    private String appUrl;
    
    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final StoredProcedureService storedProcedureService;
    
    @Override
    public String createPaymentUrl(PaymentRequestDTO request) {
        if ("VNPAY".equalsIgnoreCase(request.getPaymentMethod())) {
            return createVnpayUrl(request);
        } else if ("MOMO".equalsIgnoreCase(request.getPaymentMethod())) {
            return createMomoUrl(request);
        } else {
            throw new RuntimeException("Phương thức thanh toán không được hỗ trợ: " + request.getPaymentMethod());
        }
    }
    
    /**
     * Tạo payment URL cho VNPAY
     */
    private String createVnpayUrl(PaymentRequestDTO request) {
        String vnp_ReturnUrl = appUrl + "/payment/callback?method=VNPAY";
        String vnp_OrderInfo = request.getOrderDescription();
        
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        cld.add(Calendar.HOUR, 24);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnpayTmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(request.getAmount().multiply(java.math.BigDecimal.valueOf(100)).longValue()));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", String.valueOf(request.getOrderId()));
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", request.getIpAddress());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        
        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            hashData.append(fieldName).append("=");
            // URL encode value như yêu cầu của VNPAY
            hashData.append(URLEncoder.encode(vnp_Params.get(fieldName), StandardCharsets.UTF_8));
            hashData.append("&");
        }
        hashData.deleteCharAt(hashData.length() - 1);
        
        String vnp_SecureHash = hmacSHA512(vnpaySecretKey, hashData.toString());
        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);
        
        // Thêm vnp_SecureHashType như yêu cầu của VNPAY
        vnp_Params.put("vnp_SecureHashType", "SHA512");
        
        // Sắp xếp lại params (bao gồm cả vnp_SecureHash và vnp_SecureHashType) để tạo query string đúng
        List<String> sortedParams = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(sortedParams);
        
        StringBuilder queryString = new StringBuilder();
        for (String key : sortedParams) {
            queryString.append(URLEncoder.encode(key, StandardCharsets.UTF_8));
            queryString.append("=");
            queryString.append(URLEncoder.encode(vnp_Params.get(key), StandardCharsets.UTF_8));
            queryString.append("&");
        }
        queryString.deleteCharAt(queryString.length() - 1);
        
        return vnpayUrl + "?" + queryString.toString();
    }
    
    /**
     * Tạo payment URL cho MoMo
     */
    private String createMomoUrl(PaymentRequestDTO request) {
        try {
            String accessKey = momoAccessKey; // AccessKey từ MoMo
            String partnerCode = momoPartnerCode;
            String orderInfo = request.getOrderDescription();
            String partnerClientId = "default"; // MoMo partnerClientId (thường là "default")
            String callbackUrl = appUrl + "/payment/callback?method=MOMO";
            
            String lang = "vi";
            String orderId = String.valueOf(request.getOrderId());
            Long amount = request.getAmount().longValue();
            String returnUrl = appUrl + "/customer/orders";
            
            // Tạo requestId và extraData
            String requestId = UUID.randomUUID().toString();
            String extraData = "";
            
            // Tạo signature theo format của MoMo
            String rawHash = "accessKey=" + accessKey +
                           "&amount=" + amount +
                           "&callbackUrl=" + callbackUrl +
                           "&extraData=" + extraData +
                           "&ipnUrl=" + callbackUrl +
                           "&orderId=" + orderId +
                           "&orderInfo=" + orderInfo +
                           "&partnerClientId=" + partnerClientId +
                           "&partnerCode=" + partnerCode +
                           "&requestId=" + requestId;
            
            String signature = hmacSHA256(momoSecretKey, rawHash);
            
            // Build request body (sẽ POST đến MoMo API)
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("partnerCode", partnerCode);
            requestBody.put("partnerClientId", partnerClientId);
            requestBody.put("orderId", orderId);
            requestBody.put("amount", amount);
            requestBody.put("orderInfo", orderInfo);
            requestBody.put("callbackUrl", callbackUrl);
            requestBody.put("returnUrl", returnUrl);
            requestBody.put("requestId", requestId);
            requestBody.put("extraData", extraData);
            requestBody.put("lang", lang);
            requestBody.put("signature", signature);
            
            // TODO: Đây là PLACEHOLDER - cần gọi HTTP POST đến MoMo API thực tế
            // Hiện tại return về một URL giả để demo
            return momoUrl + "?orderId=" + orderId + "&amount=" + amount;
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo URL thanh toán MoMo: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean handlePaymentWebhook(PaymentWebhookDTO webhookDto) {
        try {
            // Verify checksum trước
            if (!verifyChecksum(webhookDto)) {
                return false;
            }
            
            Integer orderId = Integer.parseInt(webhookDto.getOrderId());
            Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + orderId));
            
            // Chỉ xử lý nếu đơn PENDING hoặc CONFIRMED
            boolean isSuccess = "00".equals(webhookDto.getPaymentStatus()) || "success".equalsIgnoreCase(webhookDto.getPaymentStatus());
            
            if (isSuccess && (Orders.OrderStatus.PENDING.equals(order.getStatus()) || 
                             Orders.OrderStatus.CONFIRMED.equals(order.getStatus()))) {
                // Cập nhật payment status
                order.setPaymentStatus(Orders.PaymentStatus.PAID);
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);
                
                // Log vào lịch sử
                mocviet.entity.OrderStatusHistory history = new mocviet.entity.OrderStatusHistory();
                history.setOrder(order);
                history.setStatus(order.getStatus().name());
                history.setNote("Thanh toán thành công qua " + webhookDto.getPaymentGateway() + 
                               " (webhook). Transaction Code: " + webhookDto.getTransactionCode());
                history.setChangedAt(LocalDateTime.now());
                orderStatusHistoryRepository.save(history);
                
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean verifyChecksum(PaymentWebhookDTO webhookDto) {
        if ("VNPAY".equalsIgnoreCase(webhookDto.getPaymentGateway())) {
            return verifyVnpayChecksum(webhookDto);
        } else if ("MOMO".equalsIgnoreCase(webhookDto.getPaymentGateway())) {
            return verifyMomoChecksum(webhookDto);
        }
        return false;
    }
    
    /**
     * Verify VNPAY checksum
     */
    private boolean verifyVnpayChecksum(PaymentWebhookDTO webhookDto) {
        // TODO: Implement VNPAY checksum verification logic
        return true; // Placeholder
    }
    
    /**
     * Verify MoMo checksum
     */
    private boolean verifyMomoChecksum(PaymentWebhookDTO webhookDto) {
        // TODO: Implement MoMo checksum verification logic
        return true; // Placeholder
    }
    
    /**
     * HMAC SHA512 - trả về hex UPPERCASE như yêu cầu của VNPAY
     */
    private String hmacSHA512(String key, String data) {
        try {
            Mac hmacSHA512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            hmacSHA512.init(secretKey);
            byte[] bytes = hmacSHA512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                // %02X để trả về hex UPPERCASE như yêu cầu của VNPAY
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * HMAC SHA256
     */
    private String hmacSHA256(String key, String data) {
        try {
            Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            hmacSHA256.init(secretKey);
            byte[] bytes = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

