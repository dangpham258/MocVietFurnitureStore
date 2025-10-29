package mocviet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Showroom")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Showroom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name", nullable = false, length = 120)
    private String name;
    
    @Column(name = "address", nullable = false, length = 255)
    private String address;
    
    @Column(name = "city", nullable = false, length = 100)
    private String city;
    
    @Column(name = "district", length = 100)
    private String district;
    
    @Column(name = "email", length = 120)
    private String email;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "open_hours", length = 120)
    private String openHours;
    
    @Column(name = "map_embed", columnDefinition = "TEXT")
    private String mapEmbed;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
