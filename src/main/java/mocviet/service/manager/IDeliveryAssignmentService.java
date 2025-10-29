package mocviet.service.manager;

import mocviet.dto.manager.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IDeliveryAssignmentService {
    Page<PendingOrderDTO> getPendingOrders(Pageable pageable);
    Page<PendingOrderDTO> getPendingOrdersWithKeyword(String keyword, Pageable pageable);
    Page<PendingOrderDTO> getPendingOrdersWithZoneAndKeyword(Integer zoneId, String keyword, Pageable pageable);
    List<PendingOrderDTO> getPendingOrdersByZone(Integer zoneId);
    Page<PendingOrderDTO> getAllRecentOrders(Pageable pageable);
    Page<PendingOrderDTO> getAllRecentOrdersWithKeyword(String keyword, Pageable pageable);

    List<DeliveryTeamDTO> getAvailableDeliveryTeams(Integer orderId);
    List<DeliveryTeamDTO> getAllDeliveryTeams();

    void assignDeliveryTeam(AssignDeliveryTeamRequest request, Integer managerId);
    void changeDeliveryTeam(ChangeDeliveryTeamRequest request, Integer managerId);

    List<ZoneDTO> getAllZones();
    String getOrderZone(Integer orderId);
    PendingOrderDTO getOrderDetails(Integer orderId);
}


