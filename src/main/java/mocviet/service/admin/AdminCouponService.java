package mocviet.service.admin;

import mocviet.dto.admin.CouponResponse;

import java.util.List;

public interface AdminCouponService {
    
    List<CouponResponse> getAllCoupons();
    
    CouponResponse getCouponByCode(String code);
    
    CouponResponse createCoupon(mocviet.dto.admin.CouponCreateRequest request);
    
    CouponResponse updateCoupon(String code, mocviet.dto.admin.CouponUpdateRequest request);
    
    void toggleCouponStatus(String code);
}

