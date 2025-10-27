package mocviet.service.admin;

import mocviet.dto.admin.BannerResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminBannerService {
    
    List<BannerResponse> getAllBanners();
    
    BannerResponse getBannerById(Integer id);
    
    BannerResponse createBanner(String title, String linkUrl, Boolean isActive, String orderNumber, MultipartFile image);
    
    BannerResponse updateBanner(Integer id, String title, String linkUrl, Boolean isActive, String orderNumber, MultipartFile image);
    
    void deleteBanner(Integer id);
}

