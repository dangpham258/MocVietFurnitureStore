package mocviet.repository;

import mocviet.entity.SocialLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialLinkRepository extends JpaRepository<SocialLink, Integer> {
    Optional<SocialLink> findByPlatform(String platform);
}

