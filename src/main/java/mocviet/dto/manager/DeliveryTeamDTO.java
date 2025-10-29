package mocviet.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryTeamDTO {
    
    private Integer id;
    private String name;
    private String phone;
    private Boolean isActive;
    private String userName;
    private String userEmail;
    private List<String> zoneNames;
    private Integer currentOrderCount;
    private String zoneCoverage;
}
