package mocviet.service.manager.impl;

import lombok.RequiredArgsConstructor;
import mocviet.entity.Color;
import mocviet.entity.Product;
import mocviet.entity.ProductImage;
import mocviet.repository.ColorRepository;
import mocviet.repository.ProductImageRepository;
import mocviet.repository.ProductRepository;
import mocviet.service.manager.IImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements IImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ColorRepository colorRepository;

    private static final String UPLOAD_DIR = "src/main/resources/static/images/products/";
    private static final String STATIC_URL_PREFIX = "/static/images/products/";

    @Override
    @Transactional
    public List<ProductImage> uploadProductImages(Integer productId, Integer colorId, List<MultipartFile> files) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        Color color = colorRepository.findById(colorId)
                .orElseThrow(() -> new IllegalArgumentException("Màu sắc không tồn tại"));

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Không có file ảnh nào được chọn");
        }

        for (MultipartFile file : files) {
            validateImageFile(file);
        }

        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục upload", e);
        }

        productImageRepository.deleteByProductIdAndColorId(productId, colorId);

        List<ProductImage> savedImages = files.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> {
                    try {
                        String fileName = generateUniqueFileName(file.getOriginalFilename());
                        String filePath = UPLOAD_DIR + fileName;
                        String url = STATIC_URL_PREFIX + fileName;

                        if (!url.startsWith(STATIC_URL_PREFIX)) {
                            throw new IllegalArgumentException("URL ảnh phải bắt đầu với " + STATIC_URL_PREFIX);
                        }

                        Path targetPath = Paths.get(filePath);
                        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                        ProductImage productImage = new ProductImage();
                        productImage.setProduct(product);
                        productImage.setColor(color);
                        productImage.setUrl(url);

                        return productImageRepository.save(productImage);
                    } catch (IOException e) {
                        throw new RuntimeException("Lỗi khi lưu file: " + e.getMessage(), e);
                    }
                })
                .toList();

        return savedImages;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImage> getProductImages(Integer productId) {
        return productImageRepository.findByProductId(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImage> getProductImagesByColor(Integer productId, Integer colorId) {
        return productImageRepository.findByProductAndColor(productId, colorId);
    }

    @Override
    @Transactional
    public void deleteProductImages(Integer productId, Integer colorId) {
        productImageRepository.deleteByProductIdAndColorId(productId, colorId);
    }

    @Override
    @Transactional
    public void deleteAllProductImages(Integer productId) {
        productImageRepository.deleteByProductId(productId);
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }

        if (file.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước file không được vượt quá 2MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") &&
                !contentType.equals("image/png") &&
                !contentType.equals("image/jpg"))) {
            throw new IllegalArgumentException("Chỉ chấp nhận file ảnh định dạng JPG, JPEG, PNG");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.toLowerCase().endsWith(".jpg") &&
                !originalFilename.toLowerCase().endsWith(".jpeg") &&
                !originalFilename.toLowerCase().endsWith(".png"))) {
            throw new IllegalArgumentException("File phải có phần mở rộng .jpg, .jpeg hoặc .png");
        }
    }

    private String generateUniqueFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
}


