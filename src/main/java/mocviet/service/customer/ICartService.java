package mocviet.service.customer;

import mocviet.entity.Cart;
import mocviet.entity.CartItem;
import mocviet.entity.ProductVariant;

import java.util.List;

public interface ICartService {
    
    /**
     * Lấy giỏ hàng của user hiện tại
     * @return Cart object hoặc null nếu chưa có
     */
    Cart getCurrentUserCart();
    
    /**
     * Lấy danh sách sản phẩm trong giỏ hàng của user hiện tại
     * @return List CartItem với thông tin đầy đủ
     */
    List<CartItem> getCurrentUserCartItems();
    
    /**
     * Lấy danh sách sản phẩm trong giỏ hàng với ProductImages (eager fetch)
     * @return List CartItem với thông tin đầy đủ bao gồm cả ProductImages
     */
    List<CartItem> getCurrentUserCartItemsWithImages();
    
    /**
     * Thêm sản phẩm vào giỏ hàng
     * @param variantId ID của ProductVariant
     * @param quantity Số lượng
     * @return true nếu thành công, false nếu thất bại
     */
    boolean addToCart(Integer variantId, Integer quantity);
    
    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     * @param cartItemId ID của CartItem
     * @param quantity Số lượng mới
     * @return true nếu thành công, false nếu thất bại
     */
    boolean updateCartItemQuantity(Integer cartItemId, Integer quantity);
    
    /**
     * Xóa sản phẩm khỏi giỏ hàng
     * @param cartItemId ID của CartItem
     * @return true nếu thành công, false nếu thất bại
     */
    boolean removeFromCart(Integer cartItemId);
    
    /**
     * Xóa tất cả sản phẩm khỏi giỏ hàng
     * @return true nếu thành công, false nếu thất bại
     */
    boolean clearCart();
    
    /**
     * Kiểm tra sản phẩm có tồn tại trong giỏ hàng không
     * @param variantId ID của ProductVariant
     * @return CartItem nếu tồn tại, null nếu không
     */
    CartItem findCartItemByVariantId(Integer variantId);
    
    /**
     * Tính tổng tiền tạm tính của giỏ hàng
     * @param selectedItemIds Danh sách ID của CartItem được chọn
     * @return Tổng tiền
     */
    Long calculateCartTotal(List<Integer> selectedItemIds);
    
    /**
     * Kiểm tra tồn kho cho các sản phẩm trong giỏ hàng
     * @param cartItems Danh sách CartItem
     * @return Map với key là CartItem ID và value là thông báo lỗi (nếu có)
     */
    java.util.Map<Integer, String> validateStockAvailability(List<CartItem> cartItems);
    
    /**
     * Lấy số lượng sản phẩm trong giỏ hàng
     * @return Tổng số lượng sản phẩm
     */
    int getCartItemCount();
}
