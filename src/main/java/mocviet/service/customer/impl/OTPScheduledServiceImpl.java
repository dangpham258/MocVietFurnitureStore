package mocviet.service.customer.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mocviet.repository.OTPRepository;
import mocviet.service.customer.IOTPScheduledService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OTPScheduledServiceImpl implements IOTPScheduledService {
    
    private final OTPRepository otpRepository;
    
    /**
     * Tự động dọn dẹp các OTP hết hạn mỗi 5 phút
     */
    @Override
    @Scheduled(fixedRate = 300000) // 5 phút = 300,000 ms
    @Transactional
    public void cleanupExpiredOTPs() {
        try {
            LocalDateTime now = LocalDateTime.now();
            otpRepository.deleteExpiredOTPs(now);
        } catch (Exception e) {
            log.error("Lỗi khi dọn dẹp OTP hết hạn: {}", e.getMessage());
        }
    }
}
