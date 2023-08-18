package umc.parasol.domain.history.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.parasol.domain.history.domain.History;
import umc.parasol.domain.history.domain.Process;
import umc.parasol.domain.history.dto.HistoryRes;
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


    // 손님의 대여 기록들 조회
    public ApiResponse historyList(@CurrentUser UserPrincipal user) {
        Member member = findMemberById(user.getId());
        List<History> historyList = historyRepository.findAllByMemberOrderByCreatedAtDesc(member);
        List<HistoryRes> historyResList = new ArrayList<>();
        for (History history : historyList) {
            historyResList.add(makeHistoryRes(member, history, history.getEndShop()));
        }

        return new ApiResponse(true, historyResList);
    }


    // 손님의 현재 대여 현황 조회
    public ApiResponse rentalStatus(@CurrentUser UserPrincipal user){
        Member member = findMemberById(user.getId());
        List<History> historyList = historyRepository.findAllByMemberOrderByCreatedAtDesc(member);

        //대여 내역이 존재하지 않을 때
        if(historyList.size() == 0) {
            return new ApiResponse(true, "");
        }

        History recentRental = historyList.get(0); //제일 최근 빌린 내역
        Process rentalProcess = recentRental.getProcess(); //해당 내역의 상태

        //현재 대여중이 아닌 경우: null
        if(rentalProcess == Process.CLEAR) {
            return new ApiResponse(true, "");
        } //대여중인 경우
        else {
            HistoryRes recentHistory = makeHistoryRes(member, recentRental, null);
            return new ApiResponse(true, recentHistory);
        }
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
                                ? ""
                                : history.getEndShop().getName())
                        : endShop.getName())
                .createdAt(history.getCreatedAt())
                .clearedAt(history.getClearedAt() == null ? LocalDateTime.of(9999, 12, 31, 23, 59) : history.getClearedAt())
                .process(history.getProcess())
                .build();
    }

}
