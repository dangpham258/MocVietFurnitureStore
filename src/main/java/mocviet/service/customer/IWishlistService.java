package mocviet.service.customer;

import mocviet.dto.customer.WishlistItemDTO;

import java.util.List;

public interface IWishlistService {
    
    List<WishlistItemDTO> getCurrentUserWishlist();
    
    boolean addToWishlist(Integer productId);
    
    boolean removeFromWishlist(Integer productId);
    
    boolean isInWishlist(Integer productId);
    
    int getWishlistCount();
    
    List<Integer> getWishlistProductIds();
    
    boolean removeWishlistItem(Integer wishlistId);
}

