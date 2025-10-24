package mocviet.controller.manager;

import lombok.RequiredArgsConstructor;
import mocviet.dto.manager.*;
import mocviet.entity.*;
import mocviet.service.manager.ProductService;
import mocviet.service.manager.ImageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/manager/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class ProductController {
    
    private final ProductService productService;
    private final ImageService imageService;
    
    // ===== PRODUCT LIST =====
    
    @GetMapping
    @Transactional(readOnly = true)
    public String productList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            Model model) {
        
        // Map database column names to entity field names
        String entityFieldName = mapSortFieldToEntity(sortBy);
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(entityFieldName).descending() : Sort.by(entityFieldName).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Product> products;
        if (keyword != null && !keyword.trim().isEmpty()) {
            products = productService.searchProducts(keyword, pageable);
        } else if (categoryId != null) {
            products = productService.getProductsByCategory(categoryId, pageable);
        } else {
            // Hiển thị tất cả sản phẩm (cả active và inactive)
            products = productService.getProducts(pageable);
        }
        
        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getCategories());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("pageTitle", "Quản lý sản phẩm");
        model.addAttribute("activeMenu", "products");
        
        return "manager/products/product_list";
    }
    
    // ===== CREATE PRODUCT =====
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("createProductRequest", new CreateProductRequest());
        model.addAttribute("categories", productService.getCategories());
        model.addAttribute("collections", productService.getCollections());
        model.addAttribute("colors", productService.getColors());
        model.addAttribute("pageTitle", "Tạo sản phẩm mới");
        model.addAttribute("activeMenu", "products");
        
        return "manager/products/product_create";
    }
    
    @PostMapping("/create")
    public String createProduct(
            @Valid @ModelAttribute("createProductRequest") CreateProductRequest request,
            BindingResult result,
            @RequestParam(value = "productImages", required = false) List<MultipartFile> productImages,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("categories", productService.getCategories());
            model.addAttribute("collections", productService.getCollections());
            model.addAttribute("colors", productService.getColors());
            model.addAttribute("pageTitle", "Tạo sản phẩm mới");
            model.addAttribute("activeMenu", "products");
            return "manager/products/product_create";
        }
        
        try {
            // Validate file size before processing
            if (productImages != null && !productImages.isEmpty()) {
                for (MultipartFile file : productImages) {
                    if (!file.isEmpty() && file.getSize() > 2 * 1024 * 1024) { // 2MB
                        redirectAttributes.addFlashAttribute("error", 
                            "File " + file.getOriginalFilename() + " quá lớn! Vui lòng chọn file nhỏ hơn 2MB.");
                        model.addAttribute("categories", productService.getCategories());
                        model.addAttribute("collections", productService.getCollections());
                        model.addAttribute("colors", productService.getColors());
                        model.addAttribute("pageTitle", "Tạo sản phẩm mới");
                        model.addAttribute("activeMenu", "products");
                        return "manager/products/product_create";
                    }
                }
            }
            
            Product product = productService.createProduct(request);
            
            // Upload images if provided
            if (productImages != null && !productImages.isEmpty() && 
                productImages.stream().anyMatch(file -> !file.isEmpty())) {
                try {
                    imageService.uploadProductImages(product.getId(), request.getColorId(), productImages);
                } catch (Exception e) {
                    // Log error but don't fail product creation
                    redirectAttributes.addFlashAttribute("warning", 
                        "Sản phẩm đã được tạo nhưng có lỗi khi upload ảnh: " + e.getMessage());
                }
            }
            
            redirectAttributes.addFlashAttribute("success", "Tạo sản phẩm thành công!");
            return "redirect:/manager/products";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", productService.getCategories());
            model.addAttribute("collections", productService.getCollections());
            model.addAttribute("colors", productService.getColors());
            model.addAttribute("pageTitle", "Tạo sản phẩm mới");
            model.addAttribute("activeMenu", "products");
            return "manager/products/product_create";
        }
    }
    
    // ===== UPDATE PRODUCT =====
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isEmpty()) {
            return "redirect:/manager/products?error=Product not found";
        }
        
        Product product = productOpt.get();
        UpdateProductRequest updateRequest = new UpdateProductRequest();
        updateRequest.setName(product.getName());
        updateRequest.setDescription(product.getDescription());
        updateRequest.setCategoryId(product.getCategory().getId());
        updateRequest.setCollectionId(product.getCollection() != null ? product.getCollection().getId() : null);
        
        model.addAttribute("product", product);
        model.addAttribute("updateProductRequest", updateRequest);
        model.addAttribute("categories", productService.getCategories());
        model.addAttribute("collections", productService.getCollections());
        model.addAttribute("pageTitle", "Chỉnh sửa sản phẩm");
        model.addAttribute("activeMenu", "products");
        
        return "manager/products/product_edit";
    }
    
    @PostMapping("/{id}/edit")
    public String updateProduct(
            @PathVariable Integer id,
            @Valid @ModelAttribute("updateProductRequest") UpdateProductRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (result.hasErrors()) {
            Optional<Product> productOpt = productService.getProductById(id);
            if (productOpt.isPresent()) {
                model.addAttribute("product", productOpt.get());
            }
            model.addAttribute("categories", productService.getCategories());
            model.addAttribute("collections", productService.getCollections());
            model.addAttribute("pageTitle", "Chỉnh sửa sản phẩm");
            model.addAttribute("activeMenu", "products");
            return "manager/products/product_edit";
        }
        
        try {
            productService.updateProduct(id, request);
            redirectAttributes.addFlashAttribute("success", "Cập nhật sản phẩm thành công!");
            return "redirect:/manager/products";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            Optional<Product> productOpt = productService.getProductById(id);
            if (productOpt.isPresent()) {
                model.addAttribute("product", productOpt.get());
            }
            model.addAttribute("categories", productService.getCategories());
            model.addAttribute("collections", productService.getCollections());
            model.addAttribute("pageTitle", "Chỉnh sửa sản phẩm");
            model.addAttribute("activeMenu", "products");
            return "manager/products/product_edit";
        }
    }
    
    // ===== TOGGLE PRODUCT ACTIVE =====
    
    @PostMapping("/{id}/toggle")
    public String toggleProductActive(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            productService.toggleProductActive(id);
            redirectAttributes.addFlashAttribute("success", "Thay đổi trạng thái sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/manager/products";
    }
    
    // ===== PRODUCT DETAIL =====
    
    @GetMapping("/{id}")
    public String productDetail(@PathVariable Integer id, Model model) {
        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isEmpty()) {
            return "redirect:/manager/products?error=Product not found";
        }
        
        Product product = productOpt.get();
        List<ProductVariant> variants = productService.getProductVariants(id);
        List<ProductImage> images = productService.getProductImages(id);
        
        model.addAttribute("product", product);
        model.addAttribute("variants", variants);
        model.addAttribute("images", images);
        model.addAttribute("colors", productService.getColors());
        model.addAttribute("pageTitle", "Chi tiết sản phẩm");
        model.addAttribute("activeMenu", "products");
        
        return "manager/products/product_detail";
    }
    
    // ===== VARIANT MANAGEMENT =====
    
    @GetMapping("/{id}/variants")
    public String productVariants(@PathVariable Integer id, Model model) {
        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isEmpty()) {
            return "redirect:/manager/products?error=Product not found";
        }
        
        Product product = productOpt.get();
        List<ProductVariant> variants = productService.getProductVariants(id);
        
        model.addAttribute("product", product);
        model.addAttribute("variants", variants);
        model.addAttribute("colors", productService.getColors());
        model.addAttribute("variantRequest", new ProductVariantRequest());
        model.addAttribute("pageTitle", "Quản lý biến thể");
        model.addAttribute("activeMenu", "products");
        
        return "manager/products/product_variants";
    }
    
    @PostMapping("/{id}/variants/add")
    public String addVariant(
            @PathVariable Integer id,
            @Valid @ModelAttribute("variantRequest") ProductVariantRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (result.hasErrors()) {
            Optional<Product> productOpt = productService.getProductById(id);
            if (productOpt.isPresent()) {
                model.addAttribute("product", productOpt.get());
                model.addAttribute("variants", productService.getProductVariants(id));
            }
            model.addAttribute("colors", productService.getColors());
            model.addAttribute("pageTitle", "Quản lý biến thể");
            model.addAttribute("activeMenu", "products");
            return "manager/products/product_variants";
        }
        
        try {
            productService.addVariant(id, request);
            redirectAttributes.addFlashAttribute("success", "Thêm biến thể thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/manager/products/" + id + "/variants";
    }
    
    @PostMapping("/variants/{variantId}/edit")
    public String updateVariant(
            @PathVariable Integer variantId,
            @Valid @ModelAttribute("variantRequest") ProductVariantRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ");
            return "redirect:/manager/products";
        }
        
        try {
            productService.updateVariant(variantId, request);
            redirectAttributes.addFlashAttribute("success", "Cập nhật biến thể thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/manager/products";
    }
    
    @PostMapping("/variants/{variantId}/delete")
    public String deleteVariant(
            @PathVariable Integer variantId,
            RedirectAttributes redirectAttributes) {
        
        try {
            productService.deleteVariant(variantId);
            redirectAttributes.addFlashAttribute("success", "Xóa biến thể thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/manager/products";
    }
    
    // ===== STOCK MANAGEMENT =====
    
    @GetMapping("/stock")
    public String stockManagement(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.getProducts(pageable);
        
        model.addAttribute("products", products);
        model.addAttribute("lowStockVariants", productService.getLowStockVariants(5));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "Quản lý tồn kho");
        model.addAttribute("activeMenu", "products");
        
        return "manager/products/stock_management";
    }
    
    @PostMapping("/variants/{variantId}/stock")
    public String updateStock(
            @PathVariable Integer variantId,
            @Valid @ModelAttribute("stockRequest") StockUpdateRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ");
            return "redirect:/manager/products/stock";
        }
        
        try {
            productService.updateStock(variantId, request);
            redirectAttributes.addFlashAttribute("success", "Cập nhật tồn kho thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/manager/products/stock";
    }
    
    // ===== PRICE MANAGEMENT =====
    
    @PostMapping("/variants/{variantId}/price")
    public String updatePrice(
            @PathVariable Integer variantId,
            @Valid @ModelAttribute("priceRequest") PriceUpdateRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ");
            return "redirect:/manager/products";
        }
        
        try {
            productService.updatePrice(variantId, request);
            redirectAttributes.addFlashAttribute("success", "Cập nhật giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/manager/products";
    }
    
    // ===== IMAGE MANAGEMENT =====
    
    @PostMapping("/{id}/images/upload")
    public String uploadProductImages(
            @PathVariable Integer id,
            @RequestParam("colorId") Integer colorId,
            @RequestParam("files") List<MultipartFile> files,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Validate file size before processing
            for (MultipartFile file : files) {
                if (!file.isEmpty() && file.getSize() > 2 * 1024 * 1024) { // 2MB
                    redirectAttributes.addFlashAttribute("error", 
                        "File " + file.getOriginalFilename() + " quá lớn! Vui lòng chọn file nhỏ hơn 2MB.");
                    return "redirect:/manager/products/" + id;
                }
            }
            
            imageService.uploadProductImages(id, colorId, files);
            redirectAttributes.addFlashAttribute("success", "Upload ảnh thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/manager/products/" + id;
    }
    
    @PostMapping("/{id}/images/delete")
    public String deleteProductImages(
            @PathVariable Integer id,
            @RequestParam("colorId") Integer colorId,
            RedirectAttributes redirectAttributes) {
        
        try {
            imageService.deleteProductImages(id, colorId);
            redirectAttributes.addFlashAttribute("success", "Xóa ảnh thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/manager/products/" + id;
    }
    
    @PostMapping("/{id}/variants/{variantId}/delete")
    public String deleteVariant(
            @PathVariable Integer id,
            @PathVariable Integer variantId,
            RedirectAttributes redirectAttributes) {
        
        try {
            productService.deleteVariant(variantId);
            redirectAttributes.addFlashAttribute("success", "Xóa biến thể thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/manager/products/" + id + "/variants";
    }
    
    @GetMapping("/{id}/variants/{variantId}/edit")
    public String showEditVariantForm(
            @PathVariable Integer id,
            @PathVariable Integer variantId,
            Model model) {
        
        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isEmpty()) {
            return "redirect:/manager/products?error=Product not found";
        }
        
        Optional<ProductVariant> variantOpt = productService.getVariantById(variantId);
        if (variantOpt.isEmpty()) {
            return "redirect:/manager/products/" + id + "?error=Variant not found";
        }
        
        Product product = productOpt.get();
        ProductVariant variant = variantOpt.get();
        
        // Create edit request with current data
        ProductVariantRequest editRequest = new ProductVariantRequest();
        editRequest.setColorId(variant.getColor().getId());
        editRequest.setTypeName(variant.getTypeName());
        editRequest.setSku(variant.getSku());
        editRequest.setPrice(variant.getPrice());
        editRequest.setDiscountPercent(variant.getDiscountPercent());
        editRequest.setStockQty(variant.getStockQty());
        editRequest.setPromotionType(variant.getPromotionType() != null ? variant.getPromotionType().name() : null);
        
        model.addAttribute("product", product);
        model.addAttribute("variants", productService.getProductVariants(id));
        model.addAttribute("colors", productService.getColors());
        model.addAttribute("variantRequest", editRequest);
        model.addAttribute("editingVariantId", variantId);
        model.addAttribute("pageTitle", "Chỉnh sửa biến thể");
        model.addAttribute("activeMenu", "products");
        
        return "manager/products/product_variants";
    }
    
    @PostMapping("/{id}/variants/{variantId}/update")
    public String updateVariant(
            @PathVariable Integer id,
            @PathVariable Integer variantId,
            @Valid @ModelAttribute("variantRequest") ProductVariantRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (result.hasErrors()) {
            Optional<Product> productOpt = productService.getProductById(id);
            if (productOpt.isPresent()) {
                model.addAttribute("product", productOpt.get());
                model.addAttribute("variants", productService.getProductVariants(id));
            }
            model.addAttribute("colors", productService.getColors());
            model.addAttribute("editingVariantId", variantId);
            model.addAttribute("pageTitle", "Chỉnh sửa biến thể");
            model.addAttribute("activeMenu", "products");
            return "manager/products/product_variants";
        }
        
        try {
            productService.updateVariant(variantId, request);
            redirectAttributes.addFlashAttribute("success", "Cập nhật biến thể thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/manager/products/" + id + "/variants";
    }
    
    // ===== UTILITY METHODS =====
    
    private String mapSortFieldToEntity(String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "name":
                return "name";
            case "created_at":
                return "createdAt";
            case "sold_qty":
                return "soldQty";
            case "views":
                return "views";
            case "avg_rating":
                return "avgRating";
            case "total_reviews":
                return "totalReviews";
            case "is_active":
                return "isActive";
            default:
                return "name"; // default sort by name
        }
    }
}
