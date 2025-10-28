package mocviet.service.customer;

import mocviet.dto.customer.CartItemDTO;

import java.util.List;

public interface ICartService {
    
    List<CartItemDTO> getCurrentUserCartItems();
    
    List<CartItemDTO> getCurrentUserCartItemsWithImages();
    
    boolean addToCart(Integer variantId, Integer quantity);
    
    boolean updateCartItemQuantity(Integer cartItemId, Integer quantity);
    
    boolean removeFromCart(Integer cartItemId);
    
    boolean clearCart();
    
    CartItemDTO findCartItemByVariantId(Integer variantId);
    
    Long calculateCartTotal(List<Integer> selectedItemIds);
    
    java.util.Map<Integer, String> validateStockAvailability(List<CartItemDTO> cartItems);
    
    int getCartItemCount();
}
