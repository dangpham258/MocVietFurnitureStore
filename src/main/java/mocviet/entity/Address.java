package mocviet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Address")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "receiver_name", length = 120, nullable = false)
    private String receiverName;
    
    @Column(name = "phone", length = 20, nullable = false)
    private String phone;
    
    @Column(name = "address_line", length = 255, nullable = false)
    private String addressLine;
    
    @Column(name = "city", length = 100, nullable = false)
    private String city;
    
    @Column(name = "district", length = 100)
    private String district;
    
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
