package mocviet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocviet.entity.SocialLink;

@Repository
public interface SocialLinkRepository extends JpaRepository<SocialLink, Integer> {
    Optional<SocialLink> findByPlatform(String platform);
}

