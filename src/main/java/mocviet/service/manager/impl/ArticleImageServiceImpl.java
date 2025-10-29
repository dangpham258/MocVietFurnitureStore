package mocviet.service.manager.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mocviet.entity.Article;
import mocviet.entity.ArticleImage;
import mocviet.repository.ArticleImageRepository;
import mocviet.repository.ArticleRepository;
import mocviet.service.manager.ArticleImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleImageServiceImpl implements ArticleImageService {

    private final ArticleRepository articleRepository;
    private final ArticleImageRepository articleImageRepository;

    private static final String BASE_STATIC_DIR = Paths.get("src","main","resources","static","images","articles").toString();

    @Override
    @Transactional
    public void uploadThumbnail(Integer articleId, MultipartFile file) {
        if (file == null || file.isEmpty()) return;
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Bài viết không tồn tại"));

        String filename = sanitizeFilename(Objects.requireNonNull(file.getOriginalFilename()));
        Path dir = Paths.get(BASE_STATIC_DIR, String.valueOf(articleId), "thumbnail");
        store(dir, filename, file);

        String url = "/static/images/articles/" + articleId + "/thumbnail/" + filename;
        article.setThumbnail(url);
        articleRepository.save(article);
    }

    @Override
    @Transactional
    public void uploadContentImages(Integer articleId, List<MultipartFile> files, List<String> captions) {
        if (files == null || files.isEmpty()) return;
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Bài viết không tồn tại"));

        Path dir = Paths.get(BASE_STATIC_DIR, String.valueOf(articleId), "content");
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (file == null || file.isEmpty()) continue;
        String filename = sanitizeFilename(Objects.requireNonNull(file.getOriginalFilename()));
        store(dir, filename, file);
            String url = "/static/images/articles/" + articleId + "/content/" + filename;

            ArticleImage img = new ArticleImage();
            img.setArticle(article);
            img.setUrl(url);
            if (captions != null && i < captions.size()) {
                img.setCaption(captions.get(i));
            }
            articleImageRepository.save(img);
        }
    }

    @Override
    @Transactional
    public void deleteContentImages(Integer articleId) {
        articleImageRepository.deleteByArticleId(articleId);
        // Optionally, file deletion from disk can be added here if needed.
    }

    private static void store(Path dir, String filename, MultipartFile file) {
        try {
            Files.createDirectories(dir);
            Path path = dir.resolve(filename);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            }
            if (log.isDebugEnabled()) {
                log.debug("Saved image to {} (size={} bytes)", path.toAbsolutePath(), file.getSize());
            }
        } catch (IOException e) {
            Path path = dir.resolve(filename);
            String absolute = path.toAbsolutePath().toString();
            log.error("Failed to save image to {}: {}", absolute, e.getMessage(), e);
            throw new IllegalStateException("Không thể lưu file ảnh: " + filename, e);
        }
    }

    private static String sanitizeFilename(String original) {
        String normalized = Normalizer.normalize(original, Normalizer.Form.NFD);
        String withoutDiacritics = Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalized).replaceAll("");
        String ascii = withoutDiacritics.replace('đ','d').replace('Đ','D');
        ascii = ascii.replaceAll("[^a-zA-Z0-9._-]", "_");
        // Collapse multiple underscores
        ascii = ascii.replaceAll("_+", "_");
        // Trim to a reasonable length to avoid OS limits
        if (ascii.length() > 150) {
            int dot = ascii.lastIndexOf('.');
            String ext = (dot > 0 && dot < ascii.length()-1) ? ascii.substring(dot) : "";
            String name = (dot > 0) ? ascii.substring(0, dot) : ascii;
            name = name.substring(0, Math.min(150 - ext.length(), name.length()));
            ascii = name + ext;
        }
        return ascii;
    }
}


