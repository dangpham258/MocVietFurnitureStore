package mocviet.service.admin;

import mocviet.dto.admin.CategoryResponse;

import java.util.List;

public interface AdminCategoryService {
    
    List<CategoryResponse> getAllCategories();
    
    CategoryResponse getCategoryById(Integer id);
    
    List<CategoryResponse> searchCategories(String keyword);
    
    CategoryResponse createCategory(mocviet.dto.admin.CategoryCreateRequest request);
    
    CategoryResponse updateCategory(Integer id, mocviet.dto.admin.CategoryUpdateRequest request);
    
    void toggleCategoryStatus(Integer id);
}

