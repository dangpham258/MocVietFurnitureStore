package mocviet.service.manager;

import mocviet.entity.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageService {
    List<ProductImage> uploadProductImages(Integer productId, Integer colorId, List<MultipartFile> files);
    List<ProductImage> getProductImages(Integer productId);
    List<ProductImage> getProductImagesByColor(Integer productId, Integer colorId);
    void deleteProductImages(Integer productId, Integer colorId);
    void deleteAllProductImages(Integer productId);
}
