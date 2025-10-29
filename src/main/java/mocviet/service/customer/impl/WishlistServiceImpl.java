package mocviet.service.customer.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.customer.WishlistItemDTO;
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
    public List<WishlistItemDTO> getCurrentUserWishlist() {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }
        
        List<Wishlist> wishlist = wishlistRepository.findByUserOrderByCreatedAtDesc(currentUser);
        return wishlist.stream().map(this::mapToDTO).toList();
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

    private WishlistItemDTO mapToDTO(Wishlist item) {
        WishlistItemDTO dto = new WishlistItemDTO();
        dto.setId(item.getId());
        if (item.getProduct() != null) {
            Product product = item.getProduct();
            dto.setProductId(product.getId());
            dto.setProductName(product.getName());
            dto.setProductSlug(product.getSlug());
            // Image
            if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
                dto.setImageUrl(product.getProductImages().get(0).getUrl());
            }
            // Price (nếu có variants, lấy giá variant đầu tiên)
            try {
                if (product.getVariants() != null && !product.getVariants().isEmpty()) {
                    var v = product.getVariants().get(0);
                    dto.setPrice(v.getSalePrice());
                    dto.setInStock(v.getIsActive() && v.getStockQty() > 0);
                }
            } catch (Exception ignored) {}
        }
        return dto;
    }
}

