package mocviet.service.admin.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.SocialLinkCreateRequest;
import mocviet.dto.admin.SocialLinkResponse;
import mocviet.dto.admin.SocialLinkUpdateRequest;
import mocviet.entity.SocialLink;
import mocviet.repository.SocialLinkRepository;
import mocviet.service.admin.AdminSocialLinkService;

@Service
@RequiredArgsConstructor
public class AdminSocialLinkServiceImpl implements AdminSocialLinkService {

    private final SocialLinkRepository socialLinkRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SocialLinkResponse> getAllSocialLinks() {
        List<SocialLink> links = socialLinkRepository.findAll();
        List<SocialLinkResponse> responses = new ArrayList<>();

        for (SocialLink link : links) {
            SocialLinkResponse response = new SocialLinkResponse();
            response.setId(link.getId());
            response.setPlatform(link.getPlatform());
            response.setUrl(link.getUrl());
            response.setIsActive(link.getIsActive());
            responses.add(response);
        }

        return responses;
    }

    @Override
    @Transactional
    public SocialLinkResponse updateSocialLink(Integer id, SocialLinkUpdateRequest request) {
        SocialLink link = socialLinkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Social link not found"));

        link.setUrl(request.getUrl());
        link.setIsActive(request.getIsActive());

        link = socialLinkRepository.save(link);

        SocialLinkResponse response = new SocialLinkResponse();
        response.setId(link.getId());
        response.setPlatform(link.getPlatform());
        response.setUrl(link.getUrl());
        response.setIsActive(link.getIsActive());

        return response;
    }

    @Override
    @Transactional
    public SocialLinkResponse createSocialLink(SocialLinkCreateRequest request) {
        // Kiểm tra xem platform đã tồn tại
        if (socialLinkRepository.findByPlatform(request.getPlatform()).isPresent()) {
            throw new RuntimeException("Platform đã tồn tại");
        }

        SocialLink link = new SocialLink();
        link.setPlatform(request.getPlatform());
        link.setUrl(request.getUrl());
        link.setIsActive(request.getIsActive());

        link = socialLinkRepository.save(link);

        SocialLinkResponse response = new SocialLinkResponse();
        response.setId(link.getId());
        response.setPlatform(link.getPlatform());
        response.setUrl(link.getUrl());
        response.setIsActive(link.getIsActive());

        return response;
    }
}

