package mocviet.repository;

import mocviet.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    
    List<Address> findByUserIdOrderByIsDefaultDescCreatedAtDesc(Integer userId);
    
    Optional<Address> findByIdAndUserId(Integer id, Integer userId);
    
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId")
    void clearDefaultByUserId(@Param("userId") Integer userId);
    
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = true WHERE a.id = :addressId AND a.user.id = :userId")
    void setDefaultByIdAndUserId(@Param("addressId") Integer addressId, @Param("userId") Integer userId);
    
    long countByUserId(Integer userId);
}
