package umc.parasol.domain.image.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.parasol.domain.image.domain.Image;
import umc.parasol.domain.shop.domain.Shop;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByUrl(String url);

    List<Image> findAllByShop(Shop shop);
}
