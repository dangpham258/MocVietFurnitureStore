package mocviet.service.customer;

import mocviet.dto.customer.ProductCardDTO;
import mocviet.dto.customer.ProductCriteriaDTO;
import mocviet.dto.customer.ProductDetailDTO;
import org.springframework.data.domain.Page;

public interface IProductService {
    
    Page<ProductCardDTO> findProducts(ProductCriteriaDTO criteria);
    
    ProductDetailDTO findProductDetailBySlug(String slug);
    
    // TODO: Thêm phương thức lấy các bộ lọc (categories, colors, collections)
    // FilterDTO getAvailableFilters();
}