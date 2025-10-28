package mocviet.service.manager;

import mocviet.dto.manager.*;
import mocviet.entity.Article;
import mocviet.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IArticleService {

    Article createArticle(CreateArticleRequest request, String authorUsername);
    Article updateArticle(UpdateArticleRequest request, String authorUsername);

    Page<ArticleListDTO> getArticlesByAuthor(String authorUsername,
                                             String articleType,
                                             Boolean status,
                                             String keyword,
                                             Pageable pageable);

    ArticleDetailDTO getArticleDetail(Integer id, String authorUsername);

    ArticleDashboardDTO getDashboard(String authorUsername);

    List<Product> getActiveProducts();
}


