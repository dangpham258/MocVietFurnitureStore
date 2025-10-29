package mocviet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import mocviet.entity.Showroom;

@Repository
public interface ShowroomRepository extends JpaRepository<Showroom, Integer> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Integer id);

    List<Showroom> findByIsActiveTrueOrderByCreatedAtDesc();
}

