package mocviet.dto.customer;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Integer id;
    private Integer orderItemId;
    private Integer rating;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;
    private String userFullName;
    private String managerRespone;
}


