package mocviet.service.admin;

import java.util.List;

import mocviet.dto.admin.ShowroomCreateRequest;
import mocviet.dto.admin.ShowroomResponse;
import mocviet.dto.admin.ShowroomUpdateRequest;

public interface AdminShowroomService {

    List<ShowroomResponse> getAllShowrooms();

    ShowroomResponse getShowroomById(Integer id);

    ShowroomResponse createShowroom(ShowroomCreateRequest request);

    ShowroomResponse updateShowroom(Integer id, ShowroomUpdateRequest request);

    void deleteShowroom(Integer id);

    void toggleShowroomStatus(Integer id);
}

