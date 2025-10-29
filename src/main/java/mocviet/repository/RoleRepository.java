package mocviet.repository;

import mocviet.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    
    /**
     * Tìm role theo tên
     */
    Optional<Role> findByName(String name);
    
    /**
     * Kiểm tra role đã tồn tại chưa
     */
    boolean existsByName(String name);
}