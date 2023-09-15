package umc.parasol.domain.shop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import umc.parasol.domain.common.BaseEntity;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status = 'ACTIVE'")
public class Shop extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String roadNameAddress;

    private String name;

    private String description;

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
    public void updateLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
    public void updateRoadNameAddress(String address) {
        this.roadNameAddress = address;
    }
    public void updateName(String name) {
        this.name = name;
    }
}
