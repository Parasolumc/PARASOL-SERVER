package umc.parasol.domain.common;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable=false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @Enumerated(value = EnumType.STRING)
    private Status status = Status.ACTIVE;

    public void updateStatus(Status status) {
        this.status = status;
    }

    @PrePersist
    public void prePersist() {
        ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime koreaDateTime = createdAt.atZone(koreaZoneId);
        this.createdAt = koreaDateTime.toLocalDateTime();
        this.modifiedAt = koreaDateTime.toLocalDateTime();
    }

    @PreUpdate
    public void preUpdate() {
        ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime koreaDateTime = ZonedDateTime.now(koreaZoneId);
        this.modifiedAt = koreaDateTime.toLocalDateTime();
    }
}