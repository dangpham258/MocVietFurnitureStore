package mocviet.service.admin;

import mocviet.dto.admin.ColorResponse;

import java.util.List;

public interface AdminColorService {
    
    List<ColorResponse> getAllColors();
    
    ColorResponse getColorById(Integer id);
    
    List<ColorResponse> searchColors(String keyword);
    
    ColorResponse createColor(mocviet.dto.admin.ColorCreateRequest request);
    
    ColorResponse updateColor(Integer id, mocviet.dto.admin.ColorUpdateRequest request);
    
    void toggleColorStatus(Integer id);
}

