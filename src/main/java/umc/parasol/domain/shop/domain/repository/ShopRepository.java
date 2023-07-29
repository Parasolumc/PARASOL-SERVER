package umc.parasol.domain.shop.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.parasol.domain.shop.domain.Shop;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    @Query("SELECT s FROM Shop s WHERE s.name LIKE %:searchKeyword% OR s.roadNameAddress LIKE %:searchKeyword%")
    List<Shop> searchShopByKeyword(@Param("searchKeyword") String searchKeyword);

}
