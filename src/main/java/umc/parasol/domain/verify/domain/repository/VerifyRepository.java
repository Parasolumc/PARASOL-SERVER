package umc.parasol.domain.verify.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.parasol.domain.verify.domain.Verify;

import java.util.Optional;

public interface VerifyRepository extends JpaRepository<Verify, Long> {
    Optional<Verify> findByCode(int code);
}
