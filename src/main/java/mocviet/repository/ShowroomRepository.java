package mocviet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocviet.entity.Showroom;

@Repository
public interface ShowroomRepository extends JpaRepository<Showroom, Integer> {
    
    /**
     * Check if showroom name exists (case-insensitive)
     */
    boolean existsByNameIgnoreCase(String name);
    
    /**
     * Check if showroom name exists excluding a specific ID
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Integer id);
    
}

