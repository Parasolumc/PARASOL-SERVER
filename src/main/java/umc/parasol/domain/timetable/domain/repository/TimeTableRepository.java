package umc.parasol.domain.timetable.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.parasol.domain.shop.domain.Shop;
import umc.parasol.domain.timetable.dto.TimeTable;

import java.util.List;

public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {

    List<TimeTable> findAllByShop(Shop shop);
}
