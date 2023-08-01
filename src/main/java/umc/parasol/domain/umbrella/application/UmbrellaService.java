package umc.parasol.domain.umbrella.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.domain.shop.domain.Shop;
import umc.parasol.domain.shop.domain.repository.ShopRepository;
import umc.parasol.domain.umbrella.domain.Umbrella;
import umc.parasol.domain.umbrella.domain.repository.UmbrellaRepository;
import umc.parasol.global.config.security.token.UserPrincipal;
import umc.parasol.global.payload.ApiResponse;

import java.util.List;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UmbrellaService {

    private final UmbrellaRepository umbrellaRepository;
    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;

    @Transactional
    // 초기 세팅 (처음에 Shop 등록했을 시 몇 개 우산 가지고 있는지 설정)
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


    @Transactional
    // 손님이 우산을 판매함
    public ApiResponse buyUmbrella(UserPrincipal user, Long shopId) {
        Member owner = getMember(user);
        Shop targetShop = getShop(shopId);
        Umbrella newUmbrella = Umbrella.createUmbrella(targetShop);
        umbrellaRepository.save(newUmbrella);

        return new ApiResponse(true, "판매 완료");
    }

    /*
    // 빌린 손님이 반납함 - 원래 매장에 반납하는지, 새 매장에 반납하는지 구분해야 함
    // History와도 관련됨 - HistoryService에서 처리해야 할 수도 있음
    public void getBack() {

    }
    */

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
}
