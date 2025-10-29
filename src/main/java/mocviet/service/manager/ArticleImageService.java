package mocviet.service.manager;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArticleImageService {

    void uploadThumbnail(Integer articleId, MultipartFile file);

    void uploadContentImages(Integer articleId, List<MultipartFile> files, List<String> captions);

    void deleteContentImages(Integer articleId);
}


