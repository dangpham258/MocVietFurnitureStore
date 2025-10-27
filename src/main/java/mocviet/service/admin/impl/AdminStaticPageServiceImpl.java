package mocviet.service.admin.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.StaticPageCreateRequest;
import mocviet.dto.admin.StaticPageResponse;
import mocviet.dto.admin.StaticPageUpdateRequest;
import mocviet.entity.StaticPage;
import mocviet.repository.StaticPageRepository;
import mocviet.service.admin.AdminStaticPageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminStaticPageServiceImpl implements AdminStaticPageService {

    private final StaticPageRepository staticPageRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StaticPageResponse> getAllPages() {
        return staticPageRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StaticPageResponse getPageById(Integer id) {
        StaticPage page = staticPageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trang tĩnh không tồn tại"));
        return convertToResponse(page);
    }

    @Override
    @Transactional
    public StaticPageResponse createPage(StaticPageCreateRequest request) {
        // Validate slug against reserved paths
        validateSlug(request.getSlug());
        
        // Check if slug already exists
        if (staticPageRepository.existsBySlugIgnoreCase(request.getSlug())) {
            throw new RuntimeException("Slug đã tồn tại");
        }

        // Create new static page
        StaticPage page = new StaticPage();
        page.setSlug(request.getSlug());
        page.setTitle(request.getTitle());
        page.setContent(request.getContent());
        
        // Set isActive with null check
        Boolean isActive = request.getIsActive();
        page.setIsActive(isActive != null ? isActive : true);
        
        page.setUpdatedAt(LocalDateTime.now());

        page = staticPageRepository.save(page);
        return convertToResponse(page);
    }

    @Override
    @Transactional
    public StaticPageResponse updatePage(Integer id, StaticPageUpdateRequest request) {
        StaticPage page = staticPageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trang tĩnh không tồn tại"));

        // Validate slug against reserved paths
        validateSlug(request.getSlug());
        
        // Check if slug already exists (excluding current page)
        if (!page.getSlug().equalsIgnoreCase(request.getSlug())) {
            if (staticPageRepository.existsBySlugIgnoreCaseAndIdNot(request.getSlug(), id)) {
                throw new RuntimeException("Slug đã tồn tại");
            }
        }

        // Update fields
        page.setSlug(request.getSlug());
        page.setTitle(request.getTitle());
        page.setContent(request.getContent());
        
        // Update isActive if provided
        if (request.getIsActive() != null) {
            page.setIsActive(request.getIsActive());
        }
        
        page.setUpdatedAt(LocalDateTime.now());

        page = staticPageRepository.save(page);
        return convertToResponse(page);
    }

    @Override
    @Transactional
    public void deletePage(Integer id) {
        if (!staticPageRepository.existsById(id)) {
            throw new RuntimeException("Trang tĩnh không tồn tại");
        }
        staticPageRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void togglePageStatus(Integer id) {
        StaticPage page = staticPageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trang tĩnh không tồn tại"));
        
        page.setIsActive(!page.getIsActive());
        page.setUpdatedAt(LocalDateTime.now());
        staticPageRepository.save(page);
    }

    /**
     * Convert StaticPage entity to StaticPageResponse DTO
     */
    private StaticPageResponse convertToResponse(StaticPage page) {
        StaticPageResponse response = new StaticPageResponse();
        response.setId(page.getId());
        response.setSlug(page.getSlug());
        response.setTitle(page.getTitle());
        response.setContent(page.getContent());
        response.setIsActive(page.getIsActive());
        response.setUpdatedAt(formatDate(page.getUpdatedAt()));
        return response;
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }
    
    /**
     * Validate slug against reserved paths
     */
    private void validateSlug(String slug) {
        String[] reservedPaths = {
            "login", "register", "logout", "admin", "manager", "delivery", "customer",
            "profile", "orders", "cart", "wishlist", "api", "auth", "css", "js", "images",
            "dashboard", "home"
        };
        
        String lowerSlug = slug.toLowerCase();
        for (String reserved : reservedPaths) {
            if (lowerSlug.equals(reserved) || lowerSlug.startsWith(reserved + "-")) {
                throw new RuntimeException("Slug không được bắt đầu với: " + reserved);
            }
        }
    }
}

