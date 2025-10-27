package mocviet.service.manager;

import lombok.RequiredArgsConstructor;
import mocviet.entity.Article;
import mocviet.entity.ArticleImage;
import mocviet.repository.ArticleImageRepository;
import mocviet.repository.ArticleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleImageService {
    
    private final ArticleImageRepository articleImageRepository;
    private final ArticleRepository articleRepository;
    
    private static final String UPLOAD_BASE_DIR = "src/main/resources/static/images/articles/";
    private static final String STATIC_URL_PREFIX = "/static/images/articles/";
    
    /**
     * Upload thumbnail cho bài viết
     * Cấu trúc: /static/images/articles/<type>/<slug>/thumbnail/00_<slug>.jpg
     */
    @Transactional
    public String uploadThumbnail(Integer articleId, MultipartFile file) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Bài viết không tồn tại"));
        
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File thumbnail không được để trống");
        }
        
        validateImageFile(file);
        
        try {
            String articleType = article.getArticleType().name().toLowerCase();
            String slug = article.getSlug();
            
            // Tạo đường dẫn thư mục: /articles/<type>/<slug>/thumbnail/
            String dirPath = UPLOAD_BASE_DIR + articleType + "/" + slug + "/thumbnail/";
            Files.createDirectories(Paths.get(dirPath));
            
            // Tên file: 00_<slug>.jpg
            String extension = getFileExtension(file.getOriginalFilename());
            String fileName = "00_" + slug + extension;
            String filePath = dirPath + fileName;
            String url = STATIC_URL_PREFIX + articleType + "/" + slug + "/thumbnail/" + fileName;
            
            // Validate URL format
            if (!url.startsWith(STATIC_URL_PREFIX)) {
                throw new IllegalArgumentException("URL ảnh phải bắt đầu với " + STATIC_URL_PREFIX);
            }
            
            // Xóa thumbnail cũ nếu có
            Path thumbnailDir = Paths.get(dirPath);
            if (Files.exists(thumbnailDir)) {
                Files.list(thumbnailDir)
                     .filter(Files::isRegularFile)
                     .forEach(path -> {
                         try {
                             Files.deleteIfExists(path);
                         } catch (IOException e) {
                             // Log error but continue
                         }
                     });
            }
            
            // Lưu file mới
            Path targetPath = Paths.get(filePath);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Cập nhật URL thumbnail trong article
            article.setThumbnail(url);
            articleRepository.save(article);
            
            return url;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu thumbnail: " + e.getMessage(), e);
        }
    }
    
    /**
     * Upload nhiều ảnh nội dung cho bài viết
     * Cấu trúc: /static/images/articles/<type>/<slug>/content/00_<slug>.jpg, 01_<slug>.jpg, ...
     */
    @Transactional
    public List<ArticleImage> uploadContentImages(Integer articleId, List<MultipartFile> files, List<String> captions) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Bài viết không tồn tại"));
        
        if (files == null || files.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Validate tất cả file trước khi upload
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                validateImageFile(file);
            }
        }
        
        try {
            String articleType = article.getArticleType().name().toLowerCase();
            String slug = article.getSlug();
            
            // Tạo đường dẫn thư mục: /articles/<type>/<slug>/content/
            String dirPath = UPLOAD_BASE_DIR + articleType + "/" + slug + "/content/";
            Files.createDirectories(Paths.get(dirPath));
            
            List<ArticleImage> savedImages = new ArrayList<>();
            int index = 0;
            
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                if (file.isEmpty()) continue;
                
                String extension = getFileExtension(file.getOriginalFilename());
                String fileName = String.format("%02d_%s%s", index, slug, extension);
                String filePath = dirPath + fileName;
                String url = STATIC_URL_PREFIX + articleType + "/" + slug + "/content/" + fileName;
                
                // Lưu file
                Path targetPath = Paths.get(filePath);
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                
                // Lưu vào database
                ArticleImage articleImage = new ArticleImage();
                articleImage.setArticle(article);
                articleImage.setUrl(url);
                
                if (captions != null && i < captions.size()) {
                    articleImage.setCaption(captions.get(i));
                }
                
                savedImages.add(articleImageRepository.save(articleImage));
                index++;
            }
            
            return savedImages;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu ảnh nội dung: " + e.getMessage(), e);
        }
    }
    
    /**
     * Xóa tất cả ảnh nội dung của bài viết
     */
    @Transactional
    public void deleteContentImages(Integer articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Bài viết không tồn tại"));
        
        try {
            String articleType = article.getArticleType().name().toLowerCase();
            String slug = article.getSlug();
            String dirPath = UPLOAD_BASE_DIR + articleType + "/" + slug + "/content/";
            
            Path contentDir = Paths.get(dirPath);
            if (Files.exists(contentDir)) {
                Files.list(contentDir)
                     .filter(Files::isRegularFile)
                     .forEach(path -> {
                         try {
                             Files.deleteIfExists(path);
                         } catch (IOException e) {
                             // Log error but continue
                         }
                     });
            }
            
            // Xóa trong database
            articleImageRepository.deleteByArticleId(articleId);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi xóa ảnh: " + e.getMessage(), e);
        }
    }
    
    /**
     * Lấy danh sách ảnh nội dung của bài viết
     */
    @Transactional(readOnly = true)
    public List<ArticleImage> getContentImages(Integer articleId) {
        return articleImageRepository.findByArticleId(articleId);
    }
    
    /**
     * Validate file ảnh
     */
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }
        
        // Check file size (max 2MB)
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước file không được vượt quá 2MB");
        }
        
        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && 
                                   !contentType.equals("image/png") && 
                                   !contentType.equals("image/jpg") &&
                                   !contentType.equals("image/webp"))) {
            throw new IllegalArgumentException("Chỉ chấp nhận file ảnh định dạng JPG, JPEG, PNG, WEBP");
        }
        
        // Check file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.toLowerCase().endsWith(".jpg") && 
                                        !originalFilename.toLowerCase().endsWith(".jpeg") && 
                                        !originalFilename.toLowerCase().endsWith(".png") &&
                                        !originalFilename.toLowerCase().endsWith(".webp"))) {
            throw new IllegalArgumentException("File phải có phần mở rộng .jpg, .jpeg, .png hoặc .webp");
        }
    }
    
    /**
     * Lấy phần mở rộng của file
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}

