package mocviet.dto.admin;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ZoneResponse {
    private Integer id;
    private String name;
    private String slug;
    private BigDecimal baseFee;
}

