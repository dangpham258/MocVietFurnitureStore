package mocviet.service;

import lombok.RequiredArgsConstructor;
import mocviet.entity.StaticPage;
import mocviet.repository.StaticPageRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StaticPageService {

    private final StaticPageRepository staticPageRepository;

    /**
     * Get static page by slug (for public view)
     */
    public Optional<StaticPage> getPageBySlug(String slug) {
        return staticPageRepository.findBySlugIgnoreCase(slug);
    }

    /**
     * Check if page exists and is active
     */
    public boolean isPageActive(String slug) {
        Optional<StaticPage> page = staticPageRepository.findBySlugIgnoreCase(slug);
        return page.isPresent() && page.get().getIsActive();
    }
}

