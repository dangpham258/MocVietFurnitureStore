package mocviet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "orders"})
public class Address {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "receiver_name", nullable = false, length = 120)
    private String receiverName;
    
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;
    
    @Column(name = "address_line", nullable = false, length = 255)
    private String addressLine;
    
    @Column(name = "city", nullable = false, length = 100)
    private String city;
    
    @Column(name = "district", length = 100)
    private String district;
    
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "address", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Orders> orders;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Custom hashCode and equals to avoid circular references
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(id, address.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}