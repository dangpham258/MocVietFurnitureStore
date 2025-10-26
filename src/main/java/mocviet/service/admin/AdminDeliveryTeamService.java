package mocviet.service.admin;

import mocviet.dto.admin.*;

import java.util.List;

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

