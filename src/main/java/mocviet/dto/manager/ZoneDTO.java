package mocviet.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZoneDTO {
    
    private Integer id;
    private String name;
    private String slug;
    private BigDecimal baseFee;
    private List<String> provinceNames;
    private Integer deliveryTeamCount;
    private Boolean hasActiveTeams;
}
