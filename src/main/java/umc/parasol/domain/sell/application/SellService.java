package umc.parasol.domain.sell.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.parasol.domain.member.application.MemberService;
import umc.parasol.domain.shop.application.ShopService;
import umc.parasol.domain.umbrella.application.UmbrellaService;
import umc.parasol.global.config.security.token.UserPrincipal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellService {

    private final SellService sellService;
    private final MemberService memberService;
    private final ShopService shopService;
    private final UmbrellaService umbrellaService;

    // 우산 판매
    public void sellUmbrella(UserPrincipal userPrincipal, Long shopId) {
        // 멤버 id, 매장 id, 받고 umbrella 새로 생성 후 sell 객체 생성

    }
}
