package mocviet.service.customer;

public interface IOTPScheduledService {
    
    /**
     * Tự động dọn dẹp các OTP hết hạn
     */
    void cleanupExpiredOTPs();
}
