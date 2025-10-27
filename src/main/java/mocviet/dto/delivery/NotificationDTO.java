// src/main/java/mocviet/dto/delivery/NotificationDTO.java
package mocviet.dto.delivery; // <<<--- Sửa package ở đây

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mocviet.entity.UserNotification; // Vẫn import entity từ package gốc

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Integer id;
    private String title;
    private String message;
    private LocalDateTime createdAt;

    public static NotificationDTO fromEntity(UserNotification entity) {
        if (entity == null) return null;
        return new NotificationDTO(
            entity.getId(),
            entity.getTitle(),
            entity.getMessage(),
            entity.getCreatedAt()
        );
    }
}