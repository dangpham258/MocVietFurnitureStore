package mocviet.controller.manager;

import lombok.RequiredArgsConstructor;
import mocviet.dto.manager.*;
import mocviet.entity.*;
import mocviet.service.manager.ProductService;
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
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
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
            productService.createProduct(request);
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
        
        model.addAttribute("product", product);
        model.addAttribute("variants", variants);
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
}
