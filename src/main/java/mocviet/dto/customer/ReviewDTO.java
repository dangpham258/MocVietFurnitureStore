package mocviet.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
}


