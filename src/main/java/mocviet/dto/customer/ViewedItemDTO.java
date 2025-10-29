package mocviet.dto.customer;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ViewedItemDTO {
    private Integer productId;
    private String productName;
    private String productSlug;
    private String thumbnailUrl;
    private LocalDateTime viewedAt;
}


