package umc.parasol.domain.image.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.parasol.domain.image.domain.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByUrl(String url);
}
