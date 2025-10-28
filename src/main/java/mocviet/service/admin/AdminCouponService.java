package mocviet.service.admin;

import java.util.List;

import mocviet.dto.admin.CouponResponse;

public interface AdminCouponService {

    List<CouponResponse> getAllCoupons();

    CouponResponse getCouponByCode(String code);

    CouponResponse createCoupon(mocviet.dto.admin.CouponCreateRequest request);

    CouponResponse updateCoupon(String code, mocviet.dto.admin.CouponUpdateRequest request);

    void toggleCouponStatus(String code);
}

