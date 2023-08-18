package umc.parasol.domain.member.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.parasol.domain.member.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByPhoneNumber(String phoneNumber);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByName(String name);
    Boolean existsByEmail(String email);
}
