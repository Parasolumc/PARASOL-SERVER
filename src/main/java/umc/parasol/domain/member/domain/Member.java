package umc.parasol.domain.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import umc.parasol.domain.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "status = 'ACTIVE'")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


}
