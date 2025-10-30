package mocviet.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mocviet.entity.StaticPage;
import mocviet.repository.StaticPageRepository;

@Service
@RequiredArgsConstructor
public class StaticPageService {

    private final StaticPageRepository staticPageRepository;

    /**
     * Lấy trang tĩnh theo slug
     */
    public Optional<StaticPage> getPageBySlug(String slug) {
        return staticPageRepository.findBySlugIgnoreCase(slug);
    }

    /**
     * Kiểm tra xem trang tĩnh có tồn tại và có hoạt động không
     */
    public boolean isPageActive(String slug) {
        Optional<StaticPage> page = staticPageRepository.findBySlugIgnoreCase(slug);
        return page.isPresent() && page.get().getIsActive();
    }
}

