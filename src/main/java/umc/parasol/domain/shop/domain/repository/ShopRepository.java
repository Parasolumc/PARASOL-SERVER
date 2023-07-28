package umc.parasol.domain.shop.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.parasol.domain.shop.domain.Shop;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
}
