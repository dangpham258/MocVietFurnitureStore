package mocviet.dto.admin;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ZoneResponse {
    private Integer id;
    private String name;
    private String slug;
    private BigDecimal baseFee;
}

