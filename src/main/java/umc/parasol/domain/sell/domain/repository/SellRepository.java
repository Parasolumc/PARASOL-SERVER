package umc.parasol.domain.sell.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.parasol.domain.history.domain.History;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.sell.domain.Sell;

import java.util.List;

public interface SellRepository extends JpaRepository<Sell, Long> {
    List<Sell> findByMemberId(Long memberId);

    List<Sell> findAllByMemberIdOrderByCreatedAtDesc(Long memberId); //내림차순(최근-과거)
}
