package umc.parasol.domain.umbrella.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.parasol.domain.shop.domain.Shop;
import umc.parasol.domain.umbrella.domain.Umbrella;

import java.util.List;

public interface UmbrellaRepository extends JpaRepository<Umbrella, Long> {
    List<Umbrella> findAllByShop(Shop shop);

    List<Umbrella> findAllByShopAndAvailableIsTrue(Shop shop);
}
