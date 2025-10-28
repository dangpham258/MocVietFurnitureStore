package mocviet.service.admin;

import java.util.List;

import mocviet.dto.admin.StaticPageCreateRequest;
import mocviet.dto.admin.StaticPageResponse;
import mocviet.dto.admin.StaticPageUpdateRequest;

public interface AdminStaticPageService {

    List<StaticPageResponse> getAllPages();

    StaticPageResponse getPageById(Integer id);

    StaticPageResponse createPage(StaticPageCreateRequest request);

    StaticPageResponse updatePage(Integer id, StaticPageUpdateRequest request);

    void deletePage(Integer id);

    void togglePageStatus(Integer id);
}

