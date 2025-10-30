package mocviet.service.admin.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.CouponCreateRequest;
import mocviet.dto.admin.CouponResponse;
import mocviet.dto.admin.CouponUpdateRequest;
import mocviet.entity.Coupon;
import mocviet.repository.CouponRepository;
import mocviet.service.admin.AdminCouponService;

@Service
@RequiredArgsConstructor
public class AdminCouponServiceImpl implements AdminCouponService {

    private final CouponRepository couponRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getCouponByCode(String code) {
        Coupon coupon = couponRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        return convertToResponse(coupon);
    }

    @Override
    @Transactional
    public CouponResponse createCoupon(CouponCreateRequest request) {
        // Kiểm tra xem mã giảm giá đã tồn tại
        if (couponRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("Mã giảm giá đã tồn tại");
        }

        // Kiểm tra ngày kết thúc phải sau ngày bắt đầu
        if (request.getStartDate().isAfter(request.getEndDate()) ||
            request.getStartDate().isEqual(request.getEndDate())) {
            throw new RuntimeException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        // Kiểm tra % giảm giá phải từ 0.01% đến 100%
        if (request.getDiscountPercent().compareTo(java.math.BigDecimal.ZERO) <= 0 ||
            request.getDiscountPercent().compareTo(java.math.BigDecimal.valueOf(100)) > 0) {
            throw new RuntimeException("% giảm giá phải từ 0.01% đến 100%");
        }

        Coupon coupon = new Coupon();
        coupon.setCode(request.getCode());
        coupon.setDiscountPercent(request.getDiscountPercent());
        coupon.setStartDate(request.getStartDate());
        coupon.setEndDate(request.getEndDate());
        coupon.setActive(request.getActive());
        coupon.setMinOrderAmount(request.getMinOrderAmount());

        coupon = couponRepository.save(coupon);

        return convertToResponse(coupon);
    }

    @Override
    @Transactional
    public CouponResponse updateCoupon(String code, CouponUpdateRequest request) {
        Coupon coupon = couponRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        // Kiểm tra ngày kết thúc phải sau ngày bắt đầu
        if (request.getStartDate().isAfter(request.getEndDate()) ||
            request.getStartDate().isEqual(request.getEndDate())) {
            throw new RuntimeException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        // Kiểm tra % giảm giá phải từ 0.01% đến 100%
        if (request.getDiscountPercent().compareTo(java.math.BigDecimal.ZERO) <= 0 ||
            request.getDiscountPercent().compareTo(java.math.BigDecimal.valueOf(100)) > 0) {
            throw new RuntimeException("% giảm giá phải từ 0.01% đến 100%");
        }

        coupon.setDiscountPercent(request.getDiscountPercent());
        coupon.setStartDate(request.getStartDate());
        coupon.setEndDate(request.getEndDate());
        coupon.setActive(request.getActive());
        coupon.setMinOrderAmount(request.getMinOrderAmount());

        coupon = couponRepository.save(coupon);

        return convertToResponse(coupon);
    }

    @Override
    @Transactional
    public void toggleCouponStatus(String code) {
        Coupon coupon = couponRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        coupon.setActive(!coupon.getActive());
        couponRepository.save(coupon);
    }

    private CouponResponse convertToResponse(Coupon coupon) {
        CouponResponse response = new CouponResponse();
        response.setCode(coupon.getCode());
        response.setDiscountPercent(coupon.getDiscountPercent());
        response.setStartDate(coupon.getStartDate());
        response.setEndDate(coupon.getEndDate());
        response.setActive(coupon.getActive());
        response.setMinOrderAmount(coupon.getMinOrderAmount());
        return response;
    }
}

