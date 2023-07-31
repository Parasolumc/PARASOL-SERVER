package umc.parasol.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.parasol.domain.history.domain.History;
import umc.parasol.domain.member.domain.Member;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findAllByMember(Member member);
}
