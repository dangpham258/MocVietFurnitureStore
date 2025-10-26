package mocviet.service.admin.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.ColorCreateRequest;
import mocviet.dto.admin.ColorResponse;
import mocviet.dto.admin.ColorUpdateRequest;
import mocviet.entity.Color;
import mocviet.repository.ColorRepository;
import mocviet.service.admin.AdminColorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminColorServiceImpl implements AdminColorService {
    
    private final ColorRepository colorRepository;
    
    @Override
    public List<ColorResponse> getAllColors() {
        return colorRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public ColorResponse getColorById(Integer id) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color not found"));
        return convertToResponse(color);
    }
    
    @Override
    public List<ColorResponse> searchColors(String keyword) {
        return colorRepository.findAll().stream()
                .filter(color -> 
                    (color.getName() != null && color.getName().toLowerCase().contains(keyword.toLowerCase())) ||
                    (color.getSlug() != null && color.getSlug().toLowerCase().contains(keyword.toLowerCase()))
                )
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public ColorResponse createColor(ColorCreateRequest request) {
        // Check name exists
        if (colorRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Tên màu đã tồn tại");
        }
        
        // Check slug exists
        if (colorRepository.findBySlug(request.getSlug()).isPresent()) {
            throw new RuntimeException("Slug đã tồn tại");
        }
        
        Color color = new Color();
        color.setName(request.getName());
        color.setSlug(request.getSlug());
        color.setHex(request.getHex());
        color.setIsActive(request.getIsActive());
        
        color = colorRepository.save(color);
        
        return convertToResponse(color);
    }
    
    @Override
    @Transactional
    public ColorResponse updateColor(Integer id, ColorUpdateRequest request) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color not found"));
        
        // Check name uniqueness if changed
        if (!color.getName().equals(request.getName())) {
            if (colorRepository.findByName(request.getName()).isPresent()) {
                throw new RuntimeException("Tên màu đã tồn tại");
            }
        }
        
        // Check slug uniqueness if changed
        if (!color.getSlug().equals(request.getSlug())) {
            if (colorRepository.findBySlug(request.getSlug()).isPresent()) {
                throw new RuntimeException("Slug đã tồn tại");
            }
            
            // Check if color slug is being used in any product images
            if (color.getImages() != null && !color.getImages().isEmpty()) {
                throw new RuntimeException("Không thể thay đổi slug khi màu sắc đã có ảnh sản phẩm");
            }
        }
        
        color.setName(request.getName());
        color.setSlug(request.getSlug());
        color.setHex(request.getHex());
        color.setIsActive(request.getIsActive());
        
        color = colorRepository.save(color);
        
        return convertToResponse(color);
    }
    
    @Override
    @Transactional
    public void toggleColorStatus(Integer id) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color not found"));
        
        color.setIsActive(!color.getIsActive());
        colorRepository.save(color);
    }
    
    private ColorResponse convertToResponse(Color color) {
        ColorResponse response = new ColorResponse();
        response.setId(color.getId());
        response.setName(color.getName());
        response.setSlug(color.getSlug());
        response.setHex(color.getHex());
        response.setIsActive(color.getIsActive());
        response.setHasImages(color.getImages() != null && !color.getImages().isEmpty());
        return response;
    }
}

