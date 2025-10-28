package mocviet.service.customer.impl;

import lombok.RequiredArgsConstructor;
import mocviet.entity.Product;
import mocviet.entity.User;
import mocviet.entity.Wishlist;
import mocviet.repository.ProductRepository;
import mocviet.repository.WishlistRepository;
import mocviet.service.UserDetailsServiceImpl;
import mocviet.service.customer.IWishlistService;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements IWishlistService {
    
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserDetailsServiceImpl userDetailsService;
    
    @Override
    @Transactional(readOnly = true)
    public List<Wishlist> getCurrentUserWishlist() {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }
        
        List<Wishlist> wishlist = wishlistRepository.findByUserOrderByCreatedAtDesc(currentUser);
        
        // Initialize collections để tránh LazyInitializationException
        for (Wishlist item : wishlist) {
            if (item.getProduct() != null) {
                Product product = item.getProduct();
                
                // Khởi tạo category và collection
                if (product.getCategory() != null) {
                    Hibernate.initialize(product.getCategory());
                }
                if (product.getCollection() != null) {
                    Hibernate.initialize(product.getCollection());
                }
                
                // Khởi tạo collections để có thể sử dụng trong view
                if (product.getProductImages() != null) {
                    Hibernate.initialize(product.getProductImages());
                }
                if (product.getVariants() != null) {
                    Hibernate.initialize(product.getVariants());
                    // Khởi tạo màu sắc cho variants
                    product.getVariants().forEach(variant -> {
                        if (variant.getColor() != null) {
                            Hibernate.initialize(variant.getColor());
                        }
                    });
                }
            }
        }
        
        return wishlist;
    }
    
    @Override
    @Transactional
    public boolean addToWishlist(Integer productId) {
        try {
            User currentUser = userDetailsService.getCurrentUser();
            if (currentUser == null) {
                return false;
            }
            
            // Kiểm tra Product có tồn tại không
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty() || !productOpt.get().getIsActive()) {
                return false;
            }
            
            Product product = productOpt.get();
            
            // Kiểm tra sản phẩm đã có trong wishlist chưa
            Optional<Wishlist> existingWishlist = wishlistRepository.findByUserAndProduct(currentUser, product);
            
            if (existingWishlist.isPresent()) {
                // Sản phẩm đã có trong wishlist
                return false;
            } else {
                // Thêm mới
                Wishlist newWishlist = new Wishlist();
                newWishlist.setUser(currentUser);
                newWishlist.setProduct(product);
                wishlistRepository.save(newWishlist);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean removeFromWishlist(Integer productId) {
        try {
            User currentUser = userDetailsService.getCurrentUser();
            if (currentUser == null) {
                return false;
            }
            
            // Kiểm tra Product có tồn tại không
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty()) {
                return false;
            }
            
            Product product = productOpt.get();
            
            // Xóa khỏi wishlist
            wishlistRepository.deleteByUserAndProduct(currentUser, product);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isInWishlist(Integer productId) {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        return wishlistRepository.existsByUserAndProduct(
            currentUser, 
            productRepository.findById(productId).orElse(null)
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public int getWishlistCount() {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            return 0;
        }
        
        return wishlistRepository.countByUser(currentUser).intValue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Integer> getWishlistProductIds() {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }
        
        return wishlistRepository.findProductIdsByUser(currentUser);
    }
    
    @Override
    @Transactional
    public boolean removeWishlistItem(Integer wishlistId) {
        try {
            User currentUser = userDetailsService.getCurrentUser();
            if (currentUser == null) {
                return false;
            }
            
            Optional<Wishlist> wishlistOpt = wishlistRepository.findById(wishlistId);
            if (wishlistOpt.isEmpty()) {
                return false;
            }
            
            Wishlist wishlist = wishlistOpt.get();
            
            // Kiểm tra quyền sở hữu
            if (!wishlist.getUser().getId().equals(currentUser.getId())) {
                return false;
            }
            
            wishlistRepository.delete(wishlist);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

