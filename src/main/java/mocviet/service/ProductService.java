package mocviet.service;

import mocviet.dto.ProductCardDTO;
import mocviet.dto.ProductCriteriaDTO;
import mocviet.dto.ProductDetailDTO;
import org.springframework.data.domain.Page;

public interface ProductService {
    
    Page<ProductCardDTO> findProducts(ProductCriteriaDTO criteria);
    
    ProductDetailDTO findProductDetailBySlug(String slug);
    
    // TODO: Thêm phương thức lấy các bộ lọc (categories, colors, collections)
    // FilterDTO getAvailableFilters();
}