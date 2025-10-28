package mocviet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import mocviet.dto.customer.ProductCardDTO;
import mocviet.dto.customer.ProductCriteriaDTO;
import mocviet.dto.customer.ProductDetailDTO;
import mocviet.service.customer.IProductService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final IProductService productService;
    private final mocviet.service.customer.IViewedService viewedService;
    private final ObjectMapper objectMapper; // Dùng để chuyển DTO sang JSON cho JavaScript
    
    /**
     * Trang danh sách sản phẩm (Guest có thể xem)
     * [cite: 25, 41, 50, 51]
     */
    @GetMapping
    public String listProducts(@ModelAttribute ProductCriteriaDTO criteria, Model model) {
        
        Page<ProductCardDTO> productPage = productService.findProducts(criteria);
        
        model.addAttribute("productPage", productPage);
        model.addAttribute("criteria", criteria);
        // model.addAttribute("filters", productService.getAvailableFilters()); // TODO: Lấy bộ lọc
        
        return "products/product-list";
    }
    
    /**
     * Trang chi tiết sản phẩm (Guest có thể xem)
     * [cite: 52, 53, 61, 62, 65, 70, 346]
     */
    @GetMapping("/{slug}")
    public String productDetail(@PathVariable("slug") String slug, Model model) {
        try {
            ProductDetailDTO productDetail = productService.findProductDetailBySlug(slug);
            model.addAttribute("product", productDetail);
            
            // Chuyển danh sách variants sang JSON để JavaScript xử lý [cite: 67, 70, 71, 346]
            String variantsJson = objectMapper.writeValueAsString(productDetail.getVariants());
            model.addAttribute("variantsJson", variantsJson);
            
            // Chuyển danh sách ảnh sang JSON
            String imagesJson = objectMapper.writeValueAsString(productDetail.getImagesByColor());
            model.addAttribute("imagesJson", imagesJson);
            
            // Ghi nhận lịch sử xem (chỉ khi đã đăng nhập)
            viewedService.recordViewBySlug(slug);

            return "products/product-detail";
            
        } catch (RuntimeException | JsonProcessingException e) {
            // TODO: Xử lý 404
            return "redirect:/products";
        }
    }
}