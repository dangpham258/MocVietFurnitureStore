package mocviet.service.admin.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.CategoryCreateRequest;
import mocviet.dto.admin.CategoryResponse;
import mocviet.dto.admin.CategoryUpdateRequest;
import mocviet.entity.Category;
import mocviet.repository.CategoryRepository;
import mocviet.service.admin.AdminCategoryService;

@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<CategoryResponse> responses = categoryRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        // Set hasChildren dựa trên quan hệ cha-con
        // Create a map of all categories by ID
        java.util.Map<Integer, CategoryResponse> responseMap = responses.stream()
                .collect(java.util.stream.Collectors.toMap(CategoryResponse::getId, r -> r));

        // Đánh dấu danh mục có con và kiểm tra sản phẩm trong con
        for (CategoryResponse response : responses) {
            if (response.getParentId() == null) {
                // Kiểm tra xem danh mục nào khác có danh mục này là cha
                boolean hasChildren = responseMap.values().stream()
                        .anyMatch(r -> r.getParentId() != null && r.getParentId().equals(response.getId()));
                response.setHasChildren(hasChildren);

                // Kiểm tra xem danh mục con nào có sản phẩm (slug được sử dụng trong đường dẫn ảnh)
                // Sử dụng trường hasProducts từ CategoryResponse thay vì truy cập products trực tiếp
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
        // Kiểm tra xem tên đã tồn tại
        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Tên danh mục đã tồn tại");
        }

        // Kiểm tra xem slug đã tồn tại
        if (categoryRepository.findBySlug(request.getSlug()).isPresent()) {
            throw new RuntimeException("Slug đã tồn tại");
        }

        // Kiểm tra loại danh mục
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

        // Đặt cha nếu có
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

        // Kiểm tra tính duy nhất của tên nếu thay đổi
        if (!category.getName().equals(request.getName())) {
            if (categoryRepository.findByName(request.getName()).isPresent()) {
                throw new RuntimeException("Tên danh mục đã tồn tại");
            }
        }

        // Kiểm tra tính duy nhất của slug nếu thay đổi
        if (!category.getSlug().equals(request.getSlug())) {
            if (categoryRepository.findBySlug(request.getSlug()).isPresent()) {
                throw new RuntimeException("Slug đã tồn tại");
            }

            // Chỉ kiểm tra sử dụng sản phẩm cho loại CATEGORY (slug được sử dụng trong đường dẫn ảnh)
            // Loại COLLECTION slug không được sử dụng trong đường dẫn ảnh, vì vậy cho phép thay đổi
            if (category.getType() == Category.CategoryType.CATEGORY) {
                // Kiểm tra xem slug của danh mục có được sử dụng trong sản phẩm (trực tiếp)
                if (category.getProducts() != null && !category.getProducts().isEmpty()) {
                    throw new RuntimeException("Không thể thay đổi slug khi danh mục đã có sản phẩm");
                }

                // Kiểm tra xem danh mục con nào có sản phẩm (slug được sử dụng trong đường dẫn ảnh)
                List<Category> childCategories = categoryRepository.findByParentId(category.getId());
                for (Category child : childCategories) {
                    if (child.getProducts() != null && !child.getProducts().isEmpty()) {
                        throw new RuntimeException("Không thể thay đổi slug khi có danh mục con đã có sản phẩm");
                    }
                }
            }
        }

        // Cập nhật cha nếu thay đổi
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

        // Lưu ý: children không được load, sẽ set sau nếu cần
        response.setHasChildren(false);

        if (category.getParent() != null) {
            response.setParentId(category.getParent().getId());
            response.setParentName(category.getParent().getName());
        }

        return response;
    }
}

