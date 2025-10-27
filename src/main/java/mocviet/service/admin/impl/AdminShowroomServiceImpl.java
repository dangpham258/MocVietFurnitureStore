package mocviet.service.admin.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.ShowroomResponse;
import mocviet.dto.admin.ShowroomCreateRequest;
import mocviet.dto.admin.ShowroomUpdateRequest;
import mocviet.entity.Showroom;
import mocviet.repository.ShowroomRepository;
import mocviet.service.admin.AdminShowroomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminShowroomServiceImpl implements AdminShowroomService {
    
    private final ShowroomRepository showroomRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<ShowroomResponse> getAllShowrooms() {
        return showroomRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ShowroomResponse getShowroomById(Integer id) {
        Showroom showroom = showroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Showroom not found"));
        return convertToResponse(showroom);
    }
    
    @Override
    @Transactional
    public ShowroomResponse createShowroom(ShowroomCreateRequest request) {
        // Check if name already exists
        if (showroomRepository.existsByNameIgnoreCase(request.getName())) {
            throw new RuntimeException("Tên showroom đã tồn tại");
        }
        
        // Create new showroom
        Showroom showroom = new Showroom();
        showroom.setName(request.getName());
        showroom.setAddress(request.getAddress());
        showroom.setCity(request.getCity());
        showroom.setDistrict(request.getDistrict());
        showroom.setEmail(request.getEmail());
        showroom.setPhone(request.getPhone());
        showroom.setOpenHours(request.getOpenHours());
        showroom.setMapEmbed(request.getMapEmbed());
        // Set isActive with null check
        Boolean isActive = request.getIsActive();
        showroom.setIsActive(isActive != null ? isActive : true);
        showroom.setCreatedAt(LocalDateTime.now());
        
        showroom = showroomRepository.save(showroom);
        
        return convertToResponse(showroom);
    }
    
    @Override
    @Transactional
    public ShowroomResponse updateShowroom(Integer id, ShowroomUpdateRequest request) {
        Showroom showroom = showroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Showroom not found"));
        
        // Check if name already exists (excluding current showroom)
        if (!showroom.getName().equals(request.getName())) {
            if (showroomRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
                throw new RuntimeException("Tên showroom đã tồn tại");
            }
        }
        
        // Update fields
        showroom.setName(request.getName());
        showroom.setAddress(request.getAddress());
        showroom.setCity(request.getCity());
        showroom.setDistrict(request.getDistrict());
        showroom.setEmail(request.getEmail());
        showroom.setPhone(request.getPhone());
        showroom.setOpenHours(request.getOpenHours());
        showroom.setMapEmbed(request.getMapEmbed());
        
        // Update isActive if provided
        if (request.getIsActive() != null) {
            showroom.setIsActive(request.getIsActive());
        }
        
        showroom = showroomRepository.save(showroom);
        
        return convertToResponse(showroom);
    }
    
    @Override
    @Transactional
    public void deleteShowroom(Integer id) {
        Showroom showroom = showroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Showroom not found"));
        
        showroomRepository.delete(showroom);
    }
    
    @Override
    @Transactional
    public void toggleShowroomStatus(Integer id) {
        Showroom showroom = showroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Showroom not found"));
        
        showroom.setIsActive(!showroom.getIsActive());
        showroomRepository.save(showroom);
    }
    
    private ShowroomResponse convertToResponse(Showroom showroom) {
        ShowroomResponse response = new ShowroomResponse();
        response.setId(showroom.getId());
        response.setName(showroom.getName());
        response.setAddress(showroom.getAddress());
        response.setCity(showroom.getCity());
        response.setDistrict(showroom.getDistrict());
        response.setEmail(showroom.getEmail());
        response.setPhone(showroom.getPhone());
        response.setOpenHours(showroom.getOpenHours());
        response.setMapEmbed(showroom.getMapEmbed());
        response.setIsActive(showroom.getIsActive());
        response.setCreatedAt(formatDate(showroom.getCreatedAt()));
        return response;
    }
    
    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dateTime.format(formatter);
    }
}

