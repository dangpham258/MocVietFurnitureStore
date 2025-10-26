package mocviet.service.admin;

import mocviet.dto.admin.*;

import java.util.List;

public interface AdminSocialLinkService {
    
    List<SocialLinkResponse> getAllSocialLinks();
    
    SocialLinkResponse updateSocialLink(Integer id, SocialLinkUpdateRequest request);
    
    SocialLinkResponse createSocialLink(SocialLinkCreateRequest request);
}

