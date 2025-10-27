package mocviet.service.admin.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.BannerResponse;
import mocviet.entity.Banner;
import mocviet.repository.BannerRepository;
import mocviet.service.admin.AdminBannerService;
import mocviet.service.admin.FileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminBannerServiceImpl implements AdminBannerService {
    
    private final BannerRepository bannerRepository;
    private final FileUploadService fileUploadService;
    
    @Override
    @Transactional(readOnly = true)
    public List<BannerResponse> getAllBanners() {
        List<Banner> banners = bannerRepository.findAll();
        
        // Sort by order number extracted from image URL
        banners.sort((b1, b2) -> {
            int order1 = extractOrderNumber(b1.getImageUrl());
            int order2 = extractOrderNumber(b2.getImageUrl());
            if (order1 != order2) {
                return Integer.compare(order1, order2);
            }
            // If same order number, sort by creation date DESC
            return b2.getCreatedAt().compareTo(b1.getCreatedAt());
        });
        
        List<BannerResponse> responses = new ArrayList<>();
        for (Banner banner : banners) {
            responses.add(convertToResponse(banner));
        }
        
        return responses;
    }
    
    /**
     * Extract order number from image URL
     * Example: "/static/images/banners/12_title.jpg" -> 12
     */
    private int extractOrderNumber(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return 99; // Put invalid URLs at the end
        }
        try {
            // Extract filename from full path
            String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            
            // Extract first 2 chars before underscore
            int underscoreIndex = filename.indexOf('_');
            if (underscoreIndex < 2) {
                return 99;
            }
            
            String orderStr = filename.substring(0, 2); // First 2 chars
            return Integer.parseInt(orderStr);
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return 99; // Put invalid formats at the end
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public BannerResponse getBannerById(Integer id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found"));
        
        return convertToResponse(banner);
    }
    
    @Override
    @Transactional
    public BannerResponse createBanner(String title, String linkUrl, Boolean isActive, String orderNumber, MultipartFile image) {
        try {
            // Validate image
            if (image == null || image.isEmpty()) {
                throw new RuntimeException("Image is required");
            }
            
            if (!fileUploadService.isImage(image)) {
                throw new RuntimeException("File must be an image");
            }
            
            if (!fileUploadService.isValidSize(image)) {
                throw new RuntimeException("Image size must be less than 10MB");
            }
            
            // Use orderNumber from user (NN for filename)
            String nn;
            if (orderNumber != null && !orderNumber.isEmpty()) {
                try {
                    int orderNum = Integer.parseInt(orderNumber);
                    if (orderNum < 0 || orderNum > 99) {
                        throw new RuntimeException("Order number must be between 0 and 99");
                    }
                    nn = String.format("%02d", orderNum);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Invalid order number format");
                }
            } else {
                nn = String.format("%02d", (int) bannerRepository.countAllBanners());
            }
            
            // Use title directly - FileUploadService will sanitize it
            String key = title != null && !title.isEmpty() ? title : UUID.randomUUID().toString().substring(0, 8);
            
            // Upload image with NN and key (FileUploadService will sanitize the key)
            String imageUrl = fileUploadService.uploadBannerImageWithKey(image, nn, key);
            
            // Create banner
            Banner banner = new Banner();
            banner.setTitle(title);
            banner.setImageUrl(imageUrl);
            banner.setLinkUrl(linkUrl);
            banner.setIsActive(isActive != null ? isActive : true);
            banner.setCreatedAt(LocalDateTime.now());
            
            banner = bannerRepository.save(banner);
            
            return convertToResponse(banner);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public BannerResponse updateBanner(Integer id, String title, String linkUrl, Boolean isActive, String orderNumber, MultipartFile image) {
        try {
            Banner banner = bannerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Banner not found"));
            
            // Update text fields
            banner.setTitle(title);
            banner.setLinkUrl(linkUrl);
            if (isActive != null) {
                banner.setIsActive(isActive);
            }
            
            // Extract current order number from existing image URL
            int currentOrderNum = extractOrderNumber(banner.getImageUrl());
            
            // Validate and format new order number
            String nn = null;
            int newOrderNum = -1;
            if (orderNumber != null && !orderNumber.isEmpty()) {
                try {
                    newOrderNum = Integer.parseInt(orderNumber);
                    if (newOrderNum < 0 || newOrderNum > 99) {
                        throw new RuntimeException("Order number must be between 0 and 99");
                    }
                    nn = String.format("%02d", newOrderNum);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Invalid order number format");
                }
            }
            
            // Update image if new image is provided
            if (image != null && !image.isEmpty()) {
                if (!fileUploadService.isImage(image)) {
                    throw new RuntimeException("File must be an image");
                }
                
                if (!fileUploadService.isValidSize(image)) {
                    throw new RuntimeException("Image size must be less than 10MB");
                }
                
                // Delete old image (non-throwing)
                if (banner.getImageUrl() != null && !banner.getImageUrl().isEmpty()) {
                    fileUploadService.deleteBannerImage(banner.getImageUrl());
                }
                
                // Use orderNumber from user or current count
                if (nn == null) {
                    nn = String.format("%02d", (int) bannerRepository.countAllBanners());
                }
                
                // Use title directly - FileUploadService will sanitize it
                String key = title != null && !title.isEmpty() ? title : UUID.randomUUID().toString().substring(0, 8);
                
                // Upload new image with NN and key (FileUploadService will sanitize the key)
                String imageUrl = fileUploadService.uploadBannerImageWithKey(image, nn, key);
                banner.setImageUrl(imageUrl);
            } else if (nn != null && newOrderNum >= 0 && currentOrderNum != newOrderNum) {
                // No new image but order number changed - rename existing file
                try {
                    String oldUrl = banner.getImageUrl();
                    if (oldUrl != null && oldUrl.contains("/")) {
                        String oldFilename = oldUrl.substring(oldUrl.lastIndexOf('/') + 1);
                        
                        // Extract old key and extension
                        int underscoreIndex = oldFilename.indexOf('_');
                        if (underscoreIndex > 0) {
                            String extension = oldFilename.substring(oldFilename.lastIndexOf('.'));
                            
                            // Generate new key from title or keep old key
                            String oldKey = oldFilename.substring(underscoreIndex + 1, oldFilename.lastIndexOf('.'));
                            String key = title != null && !title.isEmpty() ? title : oldKey;
                            
                            // Sanitize key for filename
                            String keySlug = key != null && !key.isEmpty() ? 
                                sanitizeKey(key) : oldKey;
                            
                            String newFilename = String.format("%s_%s%s", nn, keySlug, extension);
                            String newImageUrl = "/static/images/banners/" + newFilename;
                            
                            // Rename file
                            java.nio.file.Path srcOldPath = java.nio.file.Paths.get("src/main/resources/static/images/banners/" + oldFilename);
                            java.nio.file.Path srcNewPath = java.nio.file.Paths.get("src/main/resources/static/images/banners/" + newFilename);
                            
                            java.nio.file.Path targetOldPath = java.nio.file.Paths.get("target/classes/static/images/banners/" + oldFilename);
                            java.nio.file.Path targetNewPath = java.nio.file.Paths.get("target/classes/static/images/banners/" + newFilename);
                            
                            if (java.nio.file.Files.exists(srcOldPath)) {
                                java.nio.file.Files.move(srcOldPath, srcNewPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            }
                            
                            if (java.nio.file.Files.exists(targetOldPath)) {
                                java.nio.file.Files.move(targetOldPath, targetNewPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            }
                            
                            banner.setImageUrl(newImageUrl);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to rename image file: " + e.getMessage());
                }
            }
            
            banner = bannerRepository.save(banner);
            
            return convertToResponse(banner);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update image: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public void deleteBanner(Integer id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found"));
        
        // Delete image file (non-throwing)
        if (banner.getImageUrl() != null && !banner.getImageUrl().isEmpty()) {
            fileUploadService.deleteBannerImage(banner.getImageUrl());
        }
        
        // Delete banner
        bannerRepository.delete(banner);
    }
    
    /**
     * Convert Banner to BannerResponse
     */
    private BannerResponse convertToResponse(Banner banner) {
        BannerResponse response = new BannerResponse();
        response.setId(banner.getId());
        response.setTitle(banner.getTitle());
        response.setImageUrl(banner.getImageUrl());
        response.setLinkUrl(banner.getLinkUrl());
        response.setIsActive(banner.getIsActive());
        response.setCreatedAt(banner.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME));
        return response;
    }
    
    /**
     * Sanitize title to be URL-friendly (same logic as FileUploadService)
     */
    private String sanitizeKey(String key) {
        if (key == null || key.isEmpty()) {
            return UUID.randomUUID().toString().substring(0, 8);
        }
        
        // Remove Vietnamese accents first
        String withoutAccents = removeVietnameseAccents(key);
        
        return withoutAccents.trim()
                  .toLowerCase()
                  .replaceAll("\\s+", "-")
                  .replaceAll("[^a-z0-9\\-]", "")
                  .replaceAll("-+", "-")
                  .replaceAll("^-|-$", "");
    }
    
    /**
     * Remove Vietnamese accents/diacritics
     */
    private String removeVietnameseAccents(String str) {
        if (str == null) return "";
        
        return str
            .replace("à", "a").replace("á", "a").replace("ả", "a").replace("ã", "a").replace("ạ", "a")
            .replace("â", "a").replace("ầ", "a").replace("ấ", "a").replace("ẩ", "a").replace("ẫ", "a").replace("ậ", "a")
            .replace("ă", "a").replace("ằ", "a").replace("ắ", "a").replace("ẳ", "a").replace("ẵ", "a").replace("ặ", "a")
            .replace("è", "e").replace("é", "e").replace("ẻ", "e").replace("ẽ", "e").replace("ẹ", "e")
            .replace("ê", "e").replace("ề", "e").replace("ế", "e").replace("ể", "e").replace("ễ", "e").replace("ệ", "e")
            .replace("đ", "d")
            .replace("ì", "i").replace("í", "i").replace("ỉ", "i").replace("ĩ", "i").replace("ị", "i")
            .replace("ò", "o").replace("ó", "o").replace("ỏ", "o").replace("õ", "o").replace("ọ", "o")
            .replace("ô", "o").replace("ồ", "o").replace("ố", "o").replace("ổ", "o").replace("ỗ", "o").replace("ộ", "o")
            .replace("ơ", "o").replace("ờ", "o").replace("ớ", "o").replace("ở", "o").replace("ỡ", "o").replace("ợ", "o")
            .replace("ù", "u").replace("ú", "u").replace("ủ", "u").replace("ũ", "u").replace("ụ", "u")
            .replace("ư", "u").replace("ừ", "u").replace("ứ", "u").replace("ử", "u").replace("ữ", "u").replace("ự", "u")
            .replace("ỳ", "y").replace("ý", "y").replace("ỷ", "y").replace("ỹ", "y").replace("ỵ", "y")
            .replace("À", "A").replace("Á", "A").replace("Ả", "A").replace("Ã", "A").replace("Ạ", "A")
            .replace("Â", "A").replace("Ầ", "A").replace("Ấ", "A").replace("Ẩ", "A").replace("Ẫ", "A").replace("Ậ", "A")
            .replace("Ă", "A").replace("Ằ", "A").replace("Ắ", "A").replace("Ẳ", "A").replace("Ẵ", "A").replace("Ặ", "A")
            .replace("È", "E").replace("É", "E").replace("Ẻ", "E").replace("Ẽ", "E").replace("Ẹ", "E")
            .replace("Ê", "E").replace("Ề", "E").replace("Ế", "E").replace("Ể", "E").replace("Ễ", "E").replace("Ệ", "E")
            .replace("Đ", "D")
            .replace("Ì", "I").replace("Í", "I").replace("Ỉ", "I").replace("Ĩ", "I").replace("Ị", "I")
            .replace("Ò", "O").replace("Ó", "O").replace("Ỏ", "O").replace("Õ", "O").replace("Ọ", "O")
            .replace("Ô", "O").replace("Ồ", "O").replace("Ố", "O").replace("Ổ", "O").replace("Ỗ", "O").replace("Ộ", "O")
            .replace("Ơ", "O").replace("Ờ", "O").replace("Ớ", "O").replace("Ở", "O").replace("Ỡ", "O").replace("Ợ", "O")
            .replace("Ù", "U").replace("Ú", "U").replace("Ủ", "U").replace("Ũ", "U").replace("Ụ", "U")
            .replace("Ư", "U").replace("Ừ", "U").replace("Ứ", "U").replace("Ử", "U").replace("Ữ", "U").replace("Ự", "U")
            .replace("Ỳ", "Y").replace("Ý", "Y").replace("Ỷ", "Y").replace("Ỹ", "Y").replace("Ỵ", "Y");
    }
}

