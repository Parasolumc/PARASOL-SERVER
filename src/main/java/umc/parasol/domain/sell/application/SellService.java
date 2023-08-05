package umc.parasol.domain.sell.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.Role;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.domain.sell.domain.Sell;
import umc.parasol.domain.sell.domain.repository.SellRepository;
import umc.parasol.domain.sell.dto.SellResultRes;
import umc.parasol.domain.shop.domain.Shop;
import umc.parasol.domain.shop.domain.repository.ShopRepository;
import umc.parasol.domain.umbrella.domain.Umbrella;
import umc.parasol.domain.umbrella.domain.repository.UmbrellaRepository;
import umc.parasol.global.DefaultAssert;
import umc.parasol.global.config.security.token.UserPrincipal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellService {

    private final MemberRepository memberRepository;
    private final SellRepository sellRepository;
    private final ShopRepository shopRepository;
    private final UmbrellaRepository umbrellaRepository;

    /**
     * 우산 판매
     * @param userPrincipal api 호출 사용자 객체
     * @param shopId 판매할 매장 ID
     */
    @Transactional
    public SellResultRes sellUmbrella(UserPrincipal userPrincipal, Long shopId) {

        Member findMember = getCustomer(userPrincipal);

        Shop targetShop = getShop(shopId);

        Umbrella newUmbrella = Umbrella.createUmbrella(targetShop);
        umbrellaRepository.save(newUmbrella);

        Sell sell = Sell.builder()
                .member(findMember)
                .shop(targetShop)
                .umbrella(newUmbrella)
                .build();

        sellRepository.save(sell);

        return SellResultRes.builder()
                .shopName(targetShop.getName())
                .roadNameAddress(targetShop.getRoadNameAddress())
                .build();

    }

    // 현재 로그인 한 멤버를 가져오는 메서드, 사장님이면 예외 처리
    private Member getCustomer(UserPrincipal user) {

        Optional<Member> member = memberRepository.findById(user.getId());
        DefaultAssert.isTrue(member.isPresent(), "사용자가 올바르지 않습니다.");
        Member findMember = member.get();

        if (findMember.getRole() == Role.OWNER) {
            throw new IllegalStateException("일반 사용자만 판매가 가능합니다.");
        }

        return findMember;
    }

    // 매장을 가져오는 메서드
    private Shop getShop(Long id) {
        return shopRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("해당 shop이 없습니다."));
    }
}
