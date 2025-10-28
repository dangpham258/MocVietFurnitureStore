package mocviet.service.customer.impl;

import lombok.RequiredArgsConstructor;
import mocviet.entity.Cart;
import mocviet.entity.CartItem;
import mocviet.entity.ProductVariant;
import mocviet.entity.User;
import mocviet.repository.CartItemRepository;
import mocviet.repository.CartRepository;
import mocviet.repository.ProductVariantRepository;
import mocviet.service.UserDetailsServiceImpl;
import mocviet.service.customer.ICartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserDetailsServiceImpl userDetailsService;
    
    @Override
    public Cart getCurrentUserCart() {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        
        return cartRepository.findByUserId(currentUser.getId()).orElse(null);
    }
    
    @Override
    public List<CartItem> getCurrentUserCartItems() {
        Cart cart = getCurrentUserCart();
        if (cart == null) {
            return List.of();
        }
        
        return cartItemRepository.findByCartIdOrderByIdAsc(cart.getId());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CartItem> getCurrentUserCartItemsWithImages() {
        Cart cart = getCurrentUserCart();
        if (cart == null) {
            return List.of();
        }
        
        return cartItemRepository.findByCartIdWithProductImages(cart.getId());
    }
    
    @Override
    @Transactional
    public boolean addToCart(Integer variantId, Integer quantity) {
        try {
            User currentUser = userDetailsService.getCurrentUser();
            if (currentUser == null) {
                return false;
            }
            
            // Kiểm tra ProductVariant có tồn tại và còn hàng không
            Optional<ProductVariant> variantOpt = productVariantRepository.findById(variantId);
            if (variantOpt.isEmpty() || !variantOpt.get().getIsActive()) {
                return false;
            }
            
            ProductVariant variant = variantOpt.get();
            if (variant.getStockQty() < quantity) {
                return false;
            }
            
            // Lấy hoặc tạo Cart
            Cart cart = cartRepository.findByUserId(currentUser.getId())
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setUser(currentUser);
                        return cartRepository.save(newCart);
                    });
            
            // Kiểm tra sản phẩm đã có trong giỏ hàng chưa
            Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variantId);
            
            if (existingItem.isPresent()) {
                // Sản phẩm đã có trong giỏ hàng, không thêm nữa
                return false;
            } else {
                // Thêm mới
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setVariant(variant);
                newItem.setQty(quantity);
                cartItemRepository.save(newItem);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean updateCartItemQuantity(Integer cartItemId, Integer quantity) {
        try {
            User currentUser = userDetailsService.getCurrentUser();
            if (currentUser == null) {
                return false;
            }
            
            Optional<CartItem> itemOpt = cartItemRepository.findById(cartItemId);
            if (itemOpt.isEmpty()) {
                return false;
            }
            
            CartItem item = itemOpt.get();
            
            // Kiểm tra quyền sở hữu
            if (!item.getCart().getUser().getId().equals(currentUser.getId())) {
                return false;
            }
            
            // Kiểm tra tồn kho
            if (quantity > item.getVariant().getStockQty()) {
                return false;
            }
            
            if (quantity <= 0) {
                // Xóa sản phẩm khỏi giỏ hàng
                cartItemRepository.delete(item);
            } else {
                item.setQty(quantity);
                cartItemRepository.save(item);
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean removeFromCart(Integer cartItemId) {
        try {
            User currentUser = userDetailsService.getCurrentUser();
            if (currentUser == null) {
                return false;
            }
            
            Optional<CartItem> itemOpt = cartItemRepository.findById(cartItemId);
            if (itemOpt.isEmpty()) {
                return false;
            }
            
            CartItem item = itemOpt.get();
            
            // Kiểm tra quyền sở hữu
            if (!item.getCart().getUser().getId().equals(currentUser.getId())) {
                return false;
            }
            
            cartItemRepository.delete(item);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean clearCart() {
        try {
            Cart cart = getCurrentUserCart();
            if (cart == null) {
                return true; // Giỏ hàng đã trống
            }
            
            cartItemRepository.deleteByCartId(cart.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public CartItem findCartItemByVariantId(Integer variantId) {
        Cart cart = getCurrentUserCart();
        if (cart == null) {
            return null;
        }
        
        return cartItemRepository.findByCartIdAndVariantId(cart.getId(), variantId).orElse(null);
    }
    
    @Override
    public Long calculateCartTotal(List<Integer> selectedItemIds) {
        if (selectedItemIds == null || selectedItemIds.isEmpty()) {
            return 0L;
        }
        
        List<CartItem> items = cartItemRepository.findAllById(selectedItemIds);
        
        return items.stream()
                .mapToLong(item -> {
                    BigDecimal salePrice = item.getVariant().getSalePrice();
                    if (salePrice != null) {
                        return salePrice.multiply(BigDecimal.valueOf(item.getQty())).longValue();
                    }
                    return 0L;
                })
                .sum();
    }
    
    @Override
    public Map<Integer, String> validateStockAvailability(List<CartItem> cartItems) {
        Map<Integer, String> errors = new HashMap<>();
        
        for (CartItem item : cartItems) {
            ProductVariant variant = item.getVariant();
            
            if (!variant.getIsActive()) {
                errors.put(item.getId(), "Sản phẩm không còn được bán");
            } else if (variant.getStockQty() < item.getQty()) {
                errors.put(item.getId(), "Số lượng tồn kho không đủ");
            } else if (variant.getStockQty() == 0) {
                errors.put(item.getId(), "Sản phẩm hiện đang hết hàng");
            }
        }
        
        return errors;
    }
    
    @Override
    public int getCartItemCount() {
        Cart cart = getCurrentUserCart();
        if (cart == null) {
            return 0;
        }
        
        return cartItemRepository.countByCartId(cart.getId());
    }
}
