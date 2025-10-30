package mocviet.service.admin;

import java.util.List;

import mocviet.dto.admin.SocialLinkCreateRequest;
import mocviet.dto.admin.SocialLinkResponse;
import mocviet.dto.admin.SocialLinkUpdateRequest;

public interface AdminSocialLinkService {

    List<SocialLinkResponse> getAllSocialLinks();

    SocialLinkResponse updateSocialLink(Integer id, SocialLinkUpdateRequest request);

    SocialLinkResponse createSocialLink(SocialLinkCreateRequest request);
}

