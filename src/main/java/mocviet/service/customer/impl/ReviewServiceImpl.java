package mocviet.service.customer.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.ReviewRequestDTO;
import mocviet.dto.UnreviewedItemDTO;
import mocviet.entity.OrderItem;
import mocviet.entity.Product;
import mocviet.entity.Review;
import mocviet.entity.User;
import mocviet.repository.OrderItemRepository;
import mocviet.repository.ReviewRepository;
import mocviet.service.UserDetailsServiceImpl;
import mocviet.service.customer.IReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements IReviewService {
    
    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserDetailsServiceImpl userDetailsService;
    
    // Thư mục lưu ảnh review
    private static final String REVIEW_IMAGE_DIR = "src/main/resources/static/images/reviews";
    
    @Override
    @Transactional(readOnly = true)
    public List<UnreviewedItemDTO> getUnreviewedItems(Integer orderId) {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }
        
        // Lấy các order items chưa đánh giá trong đơn hàng
        List<OrderItem> orderItems = orderItemRepository.findByOrderIdWithVariantAndColor(orderId);
        
        return orderItems.stream()
                .filter(item -> item.getOrder().getStatus().name().equals("DELIVERED"))
                .filter(item -> !reviewRepository.existsByOrderItemId(item.getId()))
                .map(item -> {
                    UnreviewedItemDTO dto = new UnreviewedItemDTO();
                    dto.setId(item.getId());
                    dto.setSku(item.getVariant().getSku());
                    dto.setColorName(item.getVariant().getColor().getName());
                    dto.setTypeName(item.getVariant().getTypeName());
                    dto.setQty(item.getQty());
                    dto.setProductSlug(item.getVariant().getProduct().getSlug());
                    dto.setProductName(item.getVariant().getProduct().getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean canReviewOrderItem(Integer orderItemId) {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        // Kiểm tra order item có tồn tại và thuộc về user
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElse(null);
        if (orderItem == null) {
            return false;
        }
        
        // Kiểm tra đơn hàng phải DELIVERED
        if (!orderItem.getOrder().getStatus().name().equals("DELIVERED")) {
            return false;
        }
        
        // Kiểm tra đơn hàng thuộc về user hiện tại
        if (!orderItem.getOrder().getUser().getId().equals(currentUser.getId())) {
            return false;
        }
        
        // Kiểm tra chưa đánh giá
        if (reviewRepository.existsByOrderItemId(orderItemId)) {
            return false;
        }
        
        return true;
    }
    
    @Override
    @Transactional
    public Review createReview(ReviewRequestDTO request) {
        // Validate order item
        OrderItem orderItem = orderItemRepository.findById(request.getOrderItemId())
                .orElseThrow(() -> new RuntimeException("Order item không tồn tại"));
        
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Người dùng chưa đăng nhập");
        }
        
        // Kiểm tra quyền đánh giá
        if (!canReviewOrderItem(request.getOrderItemId())) {
            throw new RuntimeException("Không thể đánh giá order item này");
        }
        
        // Tạo review
        Review review = new Review();
        review.setProduct(orderItem.getVariant().getProduct());
        review.setUser(currentUser);
        review.setOrderItem(orderItem);
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setImageUrl(request.getImageUrl());
        review.setIsHidden(false);
        
        return reviewRepository.save(review);
    }
    
    @Override
    @Transactional
    public String uploadReviewImage(MultipartFile file, Integer reviewId) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        try {
            // Validate file extension
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new RuntimeException("Tên file không hợp lệ");
            }
            
            String extension = "";
            int lastDot = originalFilename.lastIndexOf(".");
            if (lastDot > 0) {
                extension = originalFilename.substring(lastDot).toLowerCase();
            }
            
            if (!extension.matches("\\.(jpg|jpeg|png|webp)")) {
                throw new RuntimeException("Chỉ chấp nhận file ảnh (jpg, jpeg, png, webp)");
            }
            
            // Validate file size (max 2MB)
            if (file.getSize() > 2 * 1024 * 1024) {
                throw new RuntimeException("Kích thước file không vượt quá 2MB");
            }
            
            // Tạo thư mục nếu chưa có
            Path uploadDir = Paths.get(REVIEW_IMAGE_DIR, reviewId.toString());
            Files.createDirectories(uploadDir);
            
            // Lưu file theo format: 00_review_{reviewId}.{ext}
            String filename = String.format("00_review_%d%s", reviewId, extension);
            Path filePath = uploadDir.resolve(filename);
            
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Trả về đường dẫn theo convention: /static/images/reviews/{reviewId}/00_review_{reviewId}.{ext}
            return String.format("/static/images/reviews/%d/%s", reviewId, filename);
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi upload ảnh: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Review updateReviewImageUrl(Integer reviewId, String imageUrl) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review không tồn tại"));
        review.setImageUrl(imageUrl);
        return reviewRepository.save(review);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Review getReviewById(Integer reviewId) {
        return reviewRepository.findById(reviewId).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Review> getProductReviews(Integer productId) {
        return reviewRepository.findByProductIdAndIsHiddenFalse(productId);
    }
}

