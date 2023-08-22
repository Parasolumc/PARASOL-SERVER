package umc.parasol.domain.timetable.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import umc.parasol.domain.common.BaseEntity;
import umc.parasol.domain.shop.domain.Shop;
import umc.parasol.domain.timetable.domain.Day;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Where(clause = "status = 'ACTIVE'")
public class TimeTable extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private Day day;
    private String openTime;
    private String endTime;
    
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    private TimeTable(Day day, String openTime, String endTime, Shop shop) {
        this.day = day;
        this.openTime = openTime;
        this.endTime = endTime;
        this.shop = shop;
    }

    public static TimeTable from(Day day, String openTime, String endTime, Shop shop) {
        return new TimeTable(day, openTime, endTime, shop);
    }
}
