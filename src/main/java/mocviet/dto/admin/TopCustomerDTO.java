package mocviet.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopCustomerDTO {
    private int rank;
    private int customerId;
    private String customerName;
    private String customerEmail;
    private long orderCount;
    private long totalSpent;
}

