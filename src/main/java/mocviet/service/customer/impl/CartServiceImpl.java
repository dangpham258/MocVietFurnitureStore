package mocviet.service.customer.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.customer.CartItemDTO;
import mocviet.entity.Cart;
import mocviet.entity.CartItem;
import mocviet.entity.ProductVariant;
import mocviet.entity.User;
import mocviet.repository.CartItemRepository;
import mocviet.repository.CartRepository;
import mocviet.repository.ProductVariantRepository;
import mocviet.repository.ProductImageRepository;
import mocviet.entity.ProductImage;
import mocviet.service.UserDetailsServiceImpl;
import mocviet.service.customer.ICartService;
import org.springframework.stereotype.Service;
import org.hibernate.Hibernate;
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
    private final ProductImageRepository productImageRepository;
    
    private Cart getCurrentUserCart() {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        return cartRepository.findByUserId(currentUser.getId()).orElse(null);
    }
    
    @Override
    public List<CartItemDTO> getCurrentUserCartItems() {
        Cart cart = getCurrentUserCart();
        if (cart == null) {
            return List.of();
        }
        return cartItemRepository.findByCartIdOrderByIdAsc(cart.getId())
                .stream().map(this::mapToDTO).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CartItemDTO> getCurrentUserCartItemsWithImages() {
        Cart cart = getCurrentUserCart();
        if (cart == null) {
            return List.of();
        }
        return cartItemRepository.findByCartIdWithProductImages(cart.getId())
                .stream().map(this::mapToDTO).toList();
    }
    
    @Override
    @Transactional
    public boolean addToCart(Integer variantId, Integer quantity) {
        try {
            User currentUser = userDetailsService.getCurrentUser();
            if (currentUser == null) {
                return false;
            }
            
            Optional<ProductVariant> variantOpt = productVariantRepository.findById(variantId);
            if (variantOpt.isEmpty() || !variantOpt.get().getIsActive()) {
                return false;
            }
            
            ProductVariant variant = variantOpt.get();
            if (variant.getStockQty() < quantity) {
                return false;
            }
            
            Cart cart = cartRepository.findByUserId(currentUser.getId())
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setUser(currentUser);
                        return cartRepository.save(newCart);
                    });
            
            Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variantId);
            
            if (existingItem.isPresent()) {
                return false;
            } else {
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
            
            if (!item.getCart().getUser().getId().equals(currentUser.getId())) {
                return false;
            }
            
            if (quantity > item.getVariant().getStockQty()) {
                return false;
            }
            
            if (quantity <= 0) {
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
                return true;
            }
            
            cartItemRepository.deleteByCartId(cart.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public CartItemDTO findCartItemByVariantId(Integer variantId) {
        Cart cart = getCurrentUserCart();
        if (cart == null) {
            return null;
        }
        
        return cartItemRepository.findByCartIdAndVariantId(cart.getId(), variantId)
                .map(this::mapToDTO).orElse(null);
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
    public Map<Integer, String> validateStockAvailability(List<CartItemDTO> cartItems) {
        Map<Integer, String> errors = new HashMap<>();
        
        for (CartItemDTO item : cartItems) {
            ProductVariant variant = productVariantRepository.findById(item.getVariantId()).orElse(null);
            if (variant == null) {
                errors.put(item.getId(), "Sản phẩm không tồn tại");
                continue;
            }
            
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

    private CartItemDTO mapToDTO(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setVariantId(item.getVariant().getId());
        dto.setSku(item.getVariant().getSku());
        if (item.getVariant().getProduct() != null) {
            var product = item.getVariant().getProduct();
            dto.setProductName(product.getName());
            dto.setProductSlug(product.getSlug());
            // Chỉ truy cập images nếu collection đã được initialize để tránh LazyInitializationException
            if (Hibernate.isInitialized(product.getProductImages())
                && product.getProductImages() != null
                && !product.getProductImages().isEmpty()) {
                dto.setImageUrl(product.getProductImages().get(0).getUrl());
            } else {
                try {
                    Integer colorId = item.getVariant().getColor() != null ? item.getVariant().getColor().getId() : null;
                    if (colorId != null) {
                        var list = productImageRepository.findFirstByProductIdAndColorId(product.getId(), colorId);
                        if (list != null && !list.isEmpty()) {
                            dto.setImageUrl(list.get(0).getUrl());
                        }
                    }
                    if (dto.getImageUrl() == null) {
                        var listAny = productImageRepository.findByProductId(product.getId());
                        if (listAny != null && !listAny.isEmpty()) {
                            dto.setImageUrl(listAny.get(0).getUrl());
                        }
                    }
                } catch (Exception ignored) {}
            }
        }
        if (item.getVariant().getColor() != null) {
            dto.setColorName(item.getVariant().getColor().getName());
        }
        dto.setTypeName(item.getVariant().getTypeName());
        dto.setUnitPrice(item.getVariant().getSalePrice());
        dto.setStockQty(item.getVariant().getStockQty());
        dto.setQty(item.getQty());
        if (item.getVariant().getSalePrice() != null) {
            dto.setTotalPrice(item.getVariant().getSalePrice().multiply(BigDecimal.valueOf(item.getQty())));
        }
        return dto;
    }
}
