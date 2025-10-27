package mocviet.service.admin;

import mocviet.dto.admin.ShowroomResponse;
import mocviet.dto.admin.ShowroomCreateRequest;
import mocviet.dto.admin.ShowroomUpdateRequest;

import java.util.List;

public interface AdminShowroomService {
    
    /**
     * Get all showrooms
     */
    List<ShowroomResponse> getAllShowrooms();
    
    /**
     * Get showroom by ID
     */
    ShowroomResponse getShowroomById(Integer id);
    
    /**
     * Create a new showroom
     */
    ShowroomResponse createShowroom(ShowroomCreateRequest request);
    
    /**
     * Update showroom
     */
    ShowroomResponse updateShowroom(Integer id, ShowroomUpdateRequest request);
    
    /**
     * Delete showroom
     */
    void deleteShowroom(Integer id);
    
    /**
     * Toggle showroom status
     */
    void toggleShowroomStatus(Integer id);
}

