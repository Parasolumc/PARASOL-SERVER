package umc.parasol.domain.umbrella.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.domain.shop.domain.Shop;
import umc.parasol.domain.umbrella.domain.Level;
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

    @Transactional
    // 초기 세팅 (처음에 Shop 등록했을 시 몇 개 우산 가지고 있는지 설정)
    public ApiResponse init(UserPrincipal user, int count) {
        if (count > Umbrella.MAX || count <= 0)
            throw new IllegalStateException("우산 갯수가 올바르지 않습니다.");

        Member owner = getShopOwner(user);
        Shop targetShop = owner.getShop();
        IntStream.range(0, count)
                .mapToObj(i -> Umbrella.createUmbrella(targetShop))
                .forEach(umbrellaRepository::save);

        return new ApiResponse(true, "등록 완료");
    }

    /*
    @Transactional
    // 손님에게 우산을 빌려주기 (남은 우산이 있어야만 가능)
    // History와도 관련됨 - HistoryService에서 처리해야 할 수도 있음
    public void borrow(UserPrincipal user) {
        Member owner = getShopOwner(user);
        Shop targetShop = owner.getShop();
        if (isNoMoreFree(targetShop)) {
            throw new IllegalStateException("가능한 우산이 없습니다.");
        }
        Umbrella umbrella = getAnyFreeUmbrella(targetShop);
        umbrella.updateLevel(Level.USE);
    }
    */

    @Transactional
    // 손님에게 중고 우산을 구매함
    public ApiResponse buyUmbrella(UserPrincipal user) {
        Member owner = getShopOwner(user);
        Shop targetShop = owner.getShop();
        if (isFull(targetShop))
            throw new IllegalStateException("더 이상 우산을 채울 수 없습니다.");
        Umbrella newUmbrella = Umbrella.createUmbrella(targetShop);
        umbrellaRepository.save(newUmbrella);

        return new ApiResponse(true, "구입 완료");
    }

    /*
    // 빌린 손님이 반납함 - 원래 매장에 반납하는지, 새 매장에 반납하는지 구분해야 함
    // History와도 관련됨 - HistoryService에서 처리해야 할 수도 있음
    public void getBack() {

    }
    */

   // 한 매장 당 가질 수 있는 우산 갯수가 최대치를 초과했는지
    private boolean isFull(Shop shop) {
        List<Umbrella> umbrellaList = getShopUmbrellaList(shop);
        return umbrellaList.size() == Umbrella.MAX;
    }

    // 매장에 등록된 우산 중 남은 우산이 비어있는지
    private boolean isNoMoreFree(Shop shop) {
        List<Umbrella> umbrellaList = getShopUmbrellaList(shop).stream()
                .filter(umbrella -> umbrella.getLevel() == Level.FREE).toList();
        return umbrellaList.isEmpty();
    }

    // Shop에 등록된 우산 갯수 전부 가져오기 (FREE, USE 모두)
    private List<Umbrella> getShopUmbrellaList(Shop shop) {
        return umbrellaRepository.findAllByShop(shop);
    }

    // 현재 로그인 한 멤버를 가져오는 작업
    private Member getShopOwner(UserPrincipal user) {
        return memberRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalStateException("해당 member가 없습니다."));
    }

    // Shop이 가지고 있는 FREE 상태의 우산 목록 중 0번째 값을 활용
    private Umbrella getAnyFreeUmbrella(Shop shop) {
        return getShopUmbrellaList(shop).stream()
                .filter(umbrella -> umbrella.getLevel() == Level.FREE)
                .toList()
                .get(0);
    }
}
