package umc.parasol.domain.history.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.parasol.domain.history.domain.History;
import umc.parasol.domain.history.domain.Process;
import umc.parasol.domain.history.domain.dto.HistoryRes;
import umc.parasol.domain.history.domain.repository.HistoryRepository;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.domain.shop.domain.Shop;
import umc.parasol.domain.shop.domain.repository.ShopRepository;
import umc.parasol.domain.umbrella.application.UmbrellaService;
import umc.parasol.domain.umbrella.domain.Umbrella;
import umc.parasol.global.config.security.token.CurrentUser;
import umc.parasol.global.config.security.token.UserPrincipal;
import umc.parasol.global.payload.ApiResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;
    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;
    private final UmbrellaService umbrellaService;

    // 손님이 우산 결제 진행
    @Transactional
    public ApiResponse rentalUmbrella(@CurrentUser UserPrincipal user, Long shopId) {
        Shop targetShop = findShopById(shopId);
        Member member = findMemberById(user.getId());
        History history = rentalUmbrella(targetShop, member);
        historyRepository.save(history);

        HistoryRes record = makeHistoryRes(member, history, null);
        return new ApiResponse(true, record);
    }

    // 손님이 우산 반납 진행
    @Transactional
    public ApiResponse returnUmbrella(@CurrentUser UserPrincipal user, Long shopId) {
        Member member = findMemberById(user.getId());

        List<History> remainHistoryList = historyRepository.findAllByMemberOrderByCreatedAtDesc(member)
                .stream()
                .filter(history -> history.getProcess() != Process.CLEAR).toList();

        if (remainHistoryList.isEmpty())
            throw new IllegalStateException("처리되지 않은 우산 내역이 없습니다.");

        History targetHistory = remainHistoryList.get(0);
        Umbrella targetUmbrella = targetHistory.getUmbrella();
        targetHistory.updateProcess(Process.CLEAR); // History CLEAR 상태로 변경

        Shop originShop = targetHistory.getFromShop();
        Shop targetShop = findShopById(shopId);

        targetUmbrella.updateAvailable(true);

        if (originShop != targetShop) { // 빌렸던 Shop과 다르다면
            targetUmbrella.updateShop(targetShop);
            targetHistory.updateEndShop(targetShop);
        }
        targetHistory.updateClearedAt(LocalDateTime.now());
        HistoryRes record = makeHistoryRes(member, targetHistory, targetShop);
        return new ApiResponse(true, record);
    }

    // 손님의 대여 기록들 조회
    public ApiResponse historyList(@CurrentUser UserPrincipal user) {
        Member member = findMemberById(user.getId());
        List<History> historyList = historyRepository.findAllByMemberOrderByCreatedAtDesc(member);
        List<HistoryRes> historyResList = new ArrayList<>();
        for (History history : historyList) {
            historyResList.add(makeHistoryRes(member, history, null));
        }

        return new ApiResponse(true, historyResList);
    }

    private Shop findShopById(Long shopId) {
        return shopRepository.findById(shopId).orElseThrow(
                () -> new IllegalStateException("해당 shop이 없습니다.")
        );
    }

    private Member findMemberById(Long memerId) {
        return memberRepository.findById(memerId).orElseThrow(
                () -> new IllegalStateException("해당 member가 없습니다.")
        );
    }

    private HistoryRes makeHistoryRes(Member member, History history, Shop endShop) {
        return HistoryRes.builder()
                .member(member.getNickname())
                .fromShop(history.getFromShop().getName())
                .endShop((endShop == null) ?
                        (history.getEndShop() == null
                                ? null
                                : history.getEndShop().getName())
                        : endShop.getName())
                .createdAt(history.getCreatedAt())
                .clearedAt(history.getClearedAt())
                .process(history.getProcess())
                .build();
    }

    private History rentalUmbrella(Shop targetShop, Member member) {
        return History.builder()
                .cost(0)
                .process(Process.USE)
                .fromShop(targetShop)
                .endShop(null)
                .member(member)
                .umbrella(umbrellaService.getAnyFreeUmbrella(targetShop))
                .build();
    }
}
