package mocviet.dto.admin;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private Integer id;
    private String title;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
}

