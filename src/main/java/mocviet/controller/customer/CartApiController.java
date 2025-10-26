package mocviet.controller.customer;

import lombok.RequiredArgsConstructor;
import mocviet.entity.CartItem;
import mocviet.service.customer.ICartService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartApiController {
    
    private final ICartService cartService;
    
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(@RequestParam Integer variantId,
                                                        @RequestParam(defaultValue = "1") Integer quantity) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Kiểm tra sản phẩm đã có trong giỏ hàng chưa
            CartItem existingItem = cartService.findCartItemByVariantId(variantId);
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
