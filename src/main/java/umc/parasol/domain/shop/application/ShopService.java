package umc.parasol.domain.shop.application;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.Role;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.domain.shop.domain.Shop;
import umc.parasol.domain.shop.domain.repository.ShopRepository;
import umc.parasol.domain.shop.dto.ShopListRes;
import umc.parasol.domain.shop.dto.UpdateUmbrellaReq;
import umc.parasol.global.DefaultAssert;
import umc.parasol.global.config.security.token.UserPrincipal;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private final MemberRepository memberRepository;

    private final ShopRepository shopRepository;

    /**
     * 매장 리스트 조회
     * @param userPrincipal api 호출하는 사용자 객체
     */
    public List<ShopListRes> getShopList(UserPrincipal userPrincipal) {

        Optional<Member> member = memberRepository.findById(userPrincipal.getId());
        DefaultAssert.isTrue(member.isPresent(), "유저가 올바르지 않습니다.");

        List<Shop> shopList = shopRepository.findAll();

        return shopList.stream().map(
                shop -> ShopListRes.builder()
                        .id(shop.getId())
                        .shopName(shop.getName())
                        .latitude(shop.getLatitude())
                        .longitude(shop.getLongitude())
                        .roadNameAddress(shop.getRoadNameAddress())
                        .openTime(shop.getOpenTime())
                        .closeTime(shop.getCloseTime())
                        .availableUmbrella(shop.getAvailableUmbrella())
                        .unavailableUmbrella(shop.getUnavailableUmbrella())
                        .build()
        ).toList();
    }

    /**
     * 대여 가능한 우산 개수 업데이트
     * @param userPrincipal api 호출하는 사용자 객체
     * @param updateUmbrellaReq 우산 개수 업데이트 dto
     */
    @Transactional
    public ShopListRes updateUmbrellaCount(UserPrincipal userPrincipal, UpdateUmbrellaReq updateUmbrellaReq) {

        Optional<Member> member = memberRepository.findById(userPrincipal.getId());
        DefaultAssert.isTrue(member.isPresent(), "유저가 올바르지 않습니다.");
        Member findMember = member.get();

        // 사장님인지 체크
        DefaultAssert.isTrue(findMember.getRole() == Role.OWNER, "사장님만 우산 개수를 수정할 수 있습니다.");

        Optional<Shop> shop = Optional.ofNullable(findMember.getShop());
        DefaultAssert.isTrue(shop.isPresent(), "연결된 매장이 없습니다.");
        Shop findShop = shop.get();

        findShop.updateAvailableUmbrella(updateUmbrellaReq.getAvailableUmbrella());

        return ShopListRes.builder()
                .id(findShop.getId())
                .shopName(findShop.getName())
                .latitude(findShop.getLatitude())
                .longitude(findShop.getLongitude())
                .roadNameAddress(findShop.getRoadNameAddress())
                .openTime(findShop.getOpenTime())
                .closeTime(findShop.getCloseTime())
                .availableUmbrella(findShop.getAvailableUmbrella())
                .unavailableUmbrella(findShop.getUnavailableUmbrella())
                .build();
    }
}
