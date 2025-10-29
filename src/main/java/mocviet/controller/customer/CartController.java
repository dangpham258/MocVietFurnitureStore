package mocviet.controller.customer;

import lombok.RequiredArgsConstructor;
import mocviet.dto.customer.CartItemDTO;
import mocviet.service.customer.ICartService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/customer/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final ICartService cartService;
    
    @GetMapping
    public String cartPage(@RequestParam(required = false) String error, Model model) {
        List<CartItemDTO> cartItems = cartService.getCurrentUserCartItems();
        Map<Integer, String> stockErrors = cartService.validateStockAvailability(cartItems);
        
        String errorMessage = null;
        if (error != null) {
            switch (error) {
                case "empty":
                    errorMessage = "Giỏ hàng trống";
                    break;
                case "not_selected":
                    errorMessage = "Vui lòng chọn sản phẩm cần thanh toán";
                    break;
                case "not_found":
                    errorMessage = "Không tìm thấy sản phẩm được chọn hoặc sản phẩm đã bị xóa";
                    break;
                default:
                    errorMessage = "Có lỗi xảy ra";
            }
        }
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("stockErrors", stockErrors);
        model.addAttribute("cartItemCount", cartService.getCartItemCount());
        model.addAttribute("errorMessage", errorMessage);
        
        return "customer/cart";
    }
    
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(@RequestParam Integer variantId,
                                                        @RequestParam Integer quantity) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            CartItemDTO existingItem = cartService.findCartItemByVariantId(variantId);
            if (existingItem != null) {
                response.put("success", false);
                response.put("message", "Sản phẩm này đã có trong giỏ hàng của bạn");
            } else {
                boolean success = cartService.addToCart(variantId, quantity);
                if (success) {
                    response.put("success", true);
                    response.put("message", "Đã thêm sản phẩm vào giỏ hàng");
                    response.put("cartItemCount", cartService.getCartItemCount());
                } else {
                    response.put("success", false);
                    response.put("message", "Không thể thêm sản phẩm vào giỏ hàng. Vui lòng kiểm tra lại tồn kho.");
                }
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra. Vui lòng thử lại sau");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/update-quantity")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateQuantity(@RequestParam Integer cartItemId,
                                                              @RequestParam Integer quantity) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = cartService.updateCartItemQuantity(cartItemId, quantity);
            if (success) {
                response.put("success", true);
                response.put("message", "Đã cập nhật số lượng");
                
                List<CartItemDTO> cartItems = cartService.getCurrentUserCartItems();
                Map<Integer, String> stockErrors = cartService.validateStockAvailability(cartItems);
                response.put("stockErrors", stockErrors);
            } else {
                response.put("success", false);
                response.put("message", "Không thể cập nhật số lượng. Vui lòng kiểm tra lại tồn kho.");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra. Vui lòng thử lại sau");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeFromCart(@RequestParam Integer cartItemId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = cartService.removeFromCart(cartItemId);
            if (success) {
                response.put("success", true);
                response.put("message", "Đã xóa sản phẩm khỏi giỏ hàng");
                response.put("cartItemCount", cartService.getCartItemCount());
            } else {
                response.put("success", false);
                response.put("message", "Không thể xóa sản phẩm khỏi giỏ hàng");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra. Vui lòng thử lại sau");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/clear")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clearCart() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = cartService.clearCart();
            if (success) {
                response.put("success", true);
                response.put("message", "Đã xóa tất cả sản phẩm khỏi giỏ hàng");
                response.put("cartItemCount", 0);
            } else {
                response.put("success", false);
                response.put("message", "Không thể xóa giỏ hàng");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra. Vui lòng thử lại sau");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/calculate-total")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> calculateTotal(@RequestBody List<Integer> selectedItemIds) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long total = cartService.calculateCartTotal(selectedItemIds);
            response.put("success", true);
            response.put("total", total);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi tính tổng tiền");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCartItemCount() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            int count = cartService.getCartItemCount();
            response.put("success", true);
            response.put("count", count);
        } catch (Exception e) {
            response.put("success", false);
            response.put("count", 0);
        }
        
        return ResponseEntity.ok(response);
    }
}
