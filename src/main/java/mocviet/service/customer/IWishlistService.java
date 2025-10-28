package mocviet.service.customer;

import mocviet.entity.Wishlist;
import mocviet.entity.Product;

import java.util.List;

public interface IWishlistService {
    
    /**
     * Lấy danh sách yêu thích của user hiện tại
     * @return List Wishlist với thông tin đầy đủ
     */
    List<Wishlist> getCurrentUserWishlist();
    
    /**
     * Thêm sản phẩm vào wishlist
     * @param productId ID của Product
     * @return true nếu thành công, false nếu thất bại
     */
    boolean addToWishlist(Integer productId);
    
    /**
     * Xóa sản phẩm khỏi wishlist
     * @param productId ID của Product
     * @return true nếu thành công, false nếu thất bại
     */
    boolean removeFromWishlist(Integer productId);
    
    /**
     * Kiểm tra sản phẩm có trong wishlist không
     * @param productId ID của Product
     * @return true nếu có, false nếu không
     */
    boolean isInWishlist(Integer productId);
    
    /**
     * Đếm số lượng sản phẩm trong wishlist của user hiện tại
     * @return số lượng sản phẩm
     */
    int getWishlistCount();
    
    /**
     * Lấy danh sách product IDs trong wishlist của user hiện tại
     * @return List product IDs
     */
    List<Integer> getWishlistProductIds();
    
    /**
     * Xóa wishlist theo ID
     * @param wishlistId ID của Wishlist
     * @return true nếu thành công, false nếu thất bại
     */
    boolean removeWishlistItem(Integer wishlistId);
}

