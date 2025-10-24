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
    
    /**
     * Tìm địa chỉ theo user ID
     */
    List<Address> findByUserId(Integer userId);
    
    /**
     * Tìm địa chỉ theo user ID, sắp xếp theo mặc định và ngày tạo
     */
    @Query("SELECT a FROM Address a WHERE a.user.id = :userId ORDER BY a.isDefault DESC, a.createdAt DESC")
    List<Address> findByUserIdOrderByIsDefaultDescCreatedAtDesc(@Param("userId") Integer userId);
    
    /**
     * Tìm địa chỉ mặc định của user
     */
    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.isDefault = true")
    Optional<Address> findDefaultByUserId(@Param("userId") Integer userId);
    
    /**
     * Tìm địa chỉ theo ID và user ID
     */
    @Query("SELECT a FROM Address a WHERE a.id = :id AND a.user.id = :userId")
    Optional<Address> findByIdAndUserId(@Param("id") Integer id, @Param("userId") Integer userId);
    
    /**
     * Đếm số địa chỉ của user
     */
    int countByUserId(Integer userId);
    
    /**
     * Bỏ mặc định của tất cả địa chỉ của user
     */
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId")
    void clearDefaultByUserId(@Param("userId") Integer userId);
    
    /**
     * Đặt địa chỉ làm mặc định theo ID và user ID
     */
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = true WHERE a.id = :id AND a.user.id = :userId")
    void setDefaultByIdAndUserId(@Param("id") Integer id, @Param("userId") Integer userId);
}