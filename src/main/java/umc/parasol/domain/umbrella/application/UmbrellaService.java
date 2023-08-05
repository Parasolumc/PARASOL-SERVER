package umc.parasol.domain.umbrella.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.parasol.domain.history.domain.History;
import umc.parasol.domain.history.dto.HistoryRes;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.domain.sell.domain.Sell;
import umc.parasol.domain.sell.domain.repository.SellRepository;
import umc.parasol.domain.sell.dto.SellHistoryRes;
import umc.parasol.domain.sell.dto.SellResultRes;
import umc.parasol.domain.shop.domain.Shop;
import umc.parasol.domain.shop.domain.repository.ShopRepository;
import umc.parasol.domain.umbrella.domain.Umbrella;
import umc.parasol.domain.umbrella.domain.repository.UmbrellaRepository;
import umc.parasol.global.config.security.token.CurrentUser;
import umc.parasol.global.config.security.token.UserPrincipal;
import umc.parasol.global.payload.ApiResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UmbrellaService {

    private final UmbrellaRepository umbrellaRepository;
    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;
    private final SellRepository sellRepository;

    @Transactional
    // 우산 개수 추가
    public ApiResponse addUmbrella(UserPrincipal user, int count) {
        if (count <= 0)
            throw new IllegalStateException("우산 갯수가 올바르지 않습니다.");

        Member owner = getMember(user);
        Shop targetShop = owner.getShop();

        IntStream.range(0, count)
                .mapToObj(i -> Umbrella.createUmbrella(targetShop))
                .forEach(umbrellaRepository::save);

        return new ApiResponse(true, "등록 완료");
    }


    // 매장에 등록된 우산 중 남은 우산이 비어있는지
    private boolean isNoMoreFree(Shop shop) {
        List<Umbrella> umbrellaList = getShopUmbrellaList(shop).stream()
                .filter(Umbrella::isAvailable).toList();
        return umbrellaList.isEmpty();
    }

    // Shop에 등록된 우산 갯수 전부 가져오기 (FREE, USE 모두)
    private List<Umbrella> getShopUmbrellaList(Shop shop) {
        return umbrellaRepository.findAllByShop(shop);
    }

    // 현재 로그인 한 멤버를 가져오는 작업
    private Member getMember(UserPrincipal user) {
        return memberRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalStateException("해당 member가 없습니다."));
    }

    private Shop getShop(Long id) {
        return shopRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("해당 shop이 없습니다."));
    }

    @Transactional
    // Shop이 가지고 있는 FREE 상태의 우산 목록 중 0번째 값을 활용
    public Umbrella getAnyFreeUmbrella(Shop shop) {
        List<Umbrella> umbrellaList = getShopUmbrellaList(shop).stream()
                .filter(Umbrella::isAvailable)
                .toList();

        if (umbrellaList.isEmpty())
            throw new IllegalStateException("가능한 남은 우산이 없습니다.");

        Umbrella umbrella = umbrellaList.get(0);
        umbrella.updateAvailable(false);

        return umbrella;
    }


    //손님이 여태까지 판매한 우산 조회
    public ApiResponse sellList(@CurrentUser UserPrincipal user){
        Member member = getMember(user);
        List<Sell> sellList = sellRepository.findByMemberId(member.getId());
        List<SellHistoryRes> historyResList = new ArrayList<>();

        if(sellList.isEmpty()){ //판매 내역이 존재하지 않음
            return new ApiResponse(true, null);
        }
        else {
            for (Sell sell : sellList) {
                historyResList.add(makeSellHistoryRes(sell));
            }
            return new ApiResponse(true, historyResList);
        }
    }


    private SellHistoryRes makeSellHistoryRes(Sell sell) {
        return SellHistoryRes.builder()
                .sellShop(sell.getShop().getName())
                .createdAt(sell.getCreatedAt())
                .umbrellaCount(1L)
                .build();
    }

}


