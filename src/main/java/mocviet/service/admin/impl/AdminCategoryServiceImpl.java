package mocviet.service.admin.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.CategoryCreateRequest;
import mocviet.dto.admin.CategoryResponse;
import mocviet.dto.admin.CategoryUpdateRequest;
import mocviet.entity.Category;
import mocviet.repository.CategoryRepository;
import mocviet.service.admin.AdminCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {
    
    private final CategoryRepository categoryRepository;
    
    @Override
    public List<CategoryResponse> getAllCategories() {
        List<CategoryResponse> responses = categoryRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        // Set hasChildren based on parent-child relationships
        // Create a map of all categories by ID
        java.util.Map<Integer, CategoryResponse> responseMap = responses.stream()
                .collect(java.util.stream.Collectors.toMap(CategoryResponse::getId, r -> r));
        
        // Mark categories that have children AND check for products in children
        for (CategoryResponse response : responses) {
            if (response.getParentId() == null) {
                // Check if any other category has this as parent
                boolean hasChildren = responseMap.values().stream()
                        .anyMatch(r -> r.getParentId() != null && r.getParentId().equals(response.getId()));
                response.setHasChildren(hasChildren);
                
                // Check if any child category has products (slug is used in image paths)
                // Use hasProducts field from CategoryResponse instead of accessing products directly
                if (hasChildren) {
                    boolean hasProductsInChildren = responses.stream()
                            .anyMatch(r -> r.getParentId() != null && 
                                        r.getParentId().equals(response.getId()) && 
                                        r.getHasProducts());
                    if (hasProductsInChildren) {
                        response.setHasProducts(true);
                    }
                }
            }
        }
        
        return responses;
    }
    
    @Override
    public CategoryResponse getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return convertToResponse(category);
    }
    
    @Override
    public List<CategoryResponse> searchCategories(String keyword) {
        return categoryRepository.findAll().stream()
                .filter(category -> 
                    (category.getName() != null && category.getName().toLowerCase().contains(keyword.toLowerCase())) ||
                    (category.getSlug() != null && category.getSlug().toLowerCase().contains(keyword.toLowerCase()))
                )
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        // Check name exists
        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Tên danh mục đã tồn tại");
        }
        
        // Check slug exists
        if (categoryRepository.findBySlug(request.getSlug()).isPresent()) {
            throw new RuntimeException("Slug đã tồn tại");
        }
        
        // Validate type
        Category.CategoryType categoryType;
        try {
            categoryType = Category.CategoryType.valueOf(request.getType());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Loại danh mục không hợp lệ");
        }
        
        Category category = new Category();
        category.setType(categoryType);
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setIsActive(request.getIsActive());
        
        // Set parent if provided
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Danh mục cha không tồn tại"));
            category.setParent(parent);
        }
        
        category = categoryRepository.save(category);
        
        return convertToResponse(category);
    }
    
    @Override
    @Transactional
    public CategoryResponse updateCategory(Integer id, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Check name uniqueness if changed
        if (!category.getName().equals(request.getName())) {
            if (categoryRepository.findByName(request.getName()).isPresent()) {
                throw new RuntimeException("Tên danh mục đã tồn tại");
            }
        }
        
        // Check slug uniqueness if changed
        if (!category.getSlug().equals(request.getSlug())) {
            if (categoryRepository.findBySlug(request.getSlug()).isPresent()) {
                throw new RuntimeException("Slug đã tồn tại");
            }
            
            // Only check product usage for CATEGORY type (slug used in image paths)
            // COLLECTION type slug is NOT used in image paths, so allow changing
            if (category.getType() == Category.CategoryType.CATEGORY) {
                // Check if category slug is being used in any products (directly)
                if (category.getProducts() != null && !category.getProducts().isEmpty()) {
                    throw new RuntimeException("Không thể thay đổi slug khi danh mục đã có sản phẩm");
                }
                
                // Check if any child categories have products (slug is in image paths)
                List<Category> childCategories = categoryRepository.findByParentId(category.getId());
                for (Category child : childCategories) {
                    if (child.getProducts() != null && !child.getProducts().isEmpty()) {
                        throw new RuntimeException("Không thể thay đổi slug khi có danh mục con đã có sản phẩm");
                    }
                }
            }
        }
        
        // Update parent if changed
        if (request.getParentId() != null && 
            (category.getParent() == null || !category.getParent().getId().equals(request.getParentId()))) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Danh mục cha không tồn tại"));
            category.setParent(parent);
        } else if (request.getParentId() == null) {
            category.setParent(null);
        }
        
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setIsActive(request.getIsActive());
        
        category = categoryRepository.save(category);
        
        return convertToResponse(category);
    }
    
    @Override
    @Transactional
    public void toggleCategoryStatus(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        category.setIsActive(!category.getIsActive());
        categoryRepository.save(category);
    }
    
    private CategoryResponse convertToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setSlug(category.getSlug());
        response.setType(category.getType().name());
        response.setIsActive(category.getIsActive());
        response.setHasProducts(category.getProducts() != null && !category.getProducts().isEmpty());
        
        // Note: children không được load, sẽ set later nếu cần
        response.setHasChildren(false);
        
        if (category.getParent() != null) {
            response.setParentId(category.getParent().getId());
            response.setParentName(category.getParent().getName());
        }
        
        return response;
    }
}

