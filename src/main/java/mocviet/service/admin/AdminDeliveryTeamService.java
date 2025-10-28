package mocviet.service.admin;

import java.util.List;

import mocviet.dto.admin.AvailableUserResponse;
import mocviet.dto.admin.DeliveryTeamCreateRequest;
import mocviet.dto.admin.DeliveryTeamResponse;
import mocviet.dto.admin.DeliveryTeamUpdateRequest;
import mocviet.dto.admin.ZoneInfoResponse;
import mocviet.dto.admin.ZoneMappingRequest;

public interface AdminDeliveryTeamService {

    List<DeliveryTeamResponse> getAllTeams();

    List<ZoneInfoResponse> getAllZones();

    List<AvailableUserResponse> getAvailableUsers();

    DeliveryTeamResponse createTeam(DeliveryTeamCreateRequest request);

    DeliveryTeamResponse updateTeam(Integer teamId, DeliveryTeamUpdateRequest request);

    void toggleTeamStatus(Integer teamId);

    void addZoneToTeam(Integer teamId, ZoneMappingRequest request);

    void removeZoneFromTeam(Integer mappingId);
}

