package mocviet.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private Integer id;
    private String receiverName;
    private String phone;
    private String addressLine;
    private String district;
    private String city;
    private Boolean isDefault;
}


