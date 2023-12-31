package umc.parasol.domain.sell.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.Role;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.domain.notification.application.NotificationService;
import umc.parasol.domain.notification.domain.Notification;
import umc.parasol.domain.notification.domain.NotificationType;
import umc.parasol.domain.notification.domain.repository.NotificationRepository;
import umc.parasol.domain.sell.domain.Sell;
import umc.parasol.domain.sell.domain.repository.SellRepository;
import umc.parasol.domain.sell.dto.SellHistoryRes;
import umc.parasol.domain.sell.dto.SellResultRes;
import umc.parasol.domain.shop.domain.Shop;
import umc.parasol.domain.shop.domain.repository.ShopRepository;
import umc.parasol.domain.umbrella.domain.Umbrella;
import umc.parasol.domain.umbrella.domain.repository.UmbrellaRepository;
import umc.parasol.global.DefaultAssert;
import umc.parasol.global.config.security.token.CurrentUser;
import umc.parasol.global.config.security.token.UserPrincipal;
import umc.parasol.global.payload.ApiResponse;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellService {

    private final MemberRepository memberRepository;
    private final SellRepository sellRepository;
    private final ShopRepository shopRepository;
    private final UmbrellaRepository umbrellaRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    /**
     * 우산 판매
     * @param userPrincipal api 호출 사용자 객체
     * @param memberId 판매할 손님 ID
     */
    @Transactional
    public SellResultRes sellUmbrella(UserPrincipal userPrincipal, Long memberId, Long shopId) {

        Member findOwner = getOwner(userPrincipal);
        Member findMember = getCustomer(memberId);

        Shop targetShop = findOwner.getShop();

        //손님이 선택한 매장 맞는지 검증
        if(targetShop.getId() != shopId) {
            throw new IllegalStateException("손님이 선택한 매장이 아닙니다.");
        }

        Umbrella newUmbrella = Umbrella.createUmbrella(targetShop);
        umbrellaRepository.save(newUmbrella);

        Sell sell = Sell.builder()
                .member(findMember)
                .shop(targetShop)
                .umbrella(newUmbrella)
                .build();

        sellRepository.save(sell);

        //알림 생성
        Notification notification = notificationService.makeNotification(targetShop, findMember, NotificationType.SALE_COMPLETED);
        notificationRepository.save(notification);

        return SellResultRes.builder()
                .shopName(targetShop.getName())
                .roadNameAddress(targetShop.getRoadNameAddress())
                .build();

    }


    //손님이 여태까지 판매한 우산 조회
    public ApiResponse sellList(@CurrentUser UserPrincipal user){
        Member member = getCustomer(user.getId());
        List<Sell> sellList = sellRepository.findAllByMemberIdOrderByCreatedAtDesc(member.getId());
        List<SellHistoryRes> historyResList = new ArrayList<>(); //중복 있는 리스트
        List<SellHistoryRes> resultList = new ArrayList<>(); //반환할 리스트(중복제거)

        if(sellList.isEmpty()){ //판매 내역이 존재하지 않음
            return new ApiResponse(true, "");
        }
        for (Sell sell : sellList) {
            historyResList.add(makeSellHistoryRes(sell, 1L));
        }
        Map<SellHistoryRes, Long> historyMap = historyResList.stream().collect(
                Collectors.toMap(Function.identity(), value -> 1L, Long::sum));

        for (SellHistoryRes res : historyMap.keySet()) {
            resultList.add(makeSellHistoryRes(res.getSellShop(), res.getCreatedAt(), historyMap.get(res)));
        }

        //시간 순 정렬
        List<SellHistoryRes> sortedResultList = resultList.stream()
                .sorted(Comparator.comparing(SellHistoryRes::getCreatedAt)).toList();

        return new ApiResponse(true, sortedResultList);

    }


    private SellHistoryRes makeSellHistoryRes(Sell sell, Long count) {
        return SellHistoryRes.builder()
                .sellShop(sell.getShop().getName())
                .createdAt(sell.getCreatedAt().toString().substring(0, 10))
                .umbrellaCount(count)
                .build();
    }

    private SellHistoryRes makeSellHistoryRes(String shopName, String time, Long count) {
        return SellHistoryRes.builder()
                .sellShop(shopName)
                .createdAt(time)
                .umbrellaCount(count)
                .build();
    }


    // 현재 로그인 한 멤버를 가져오는 메서드, 사장님이면 예외 처리
    private Member getCustomer(Long memberId) {

        Optional<Member> member = memberRepository.findById(memberId);
        DefaultAssert.isTrue(member.isPresent(), "사용자가 올바르지 않습니다.");
        Member findMember = member.get();

        if (findMember.getRole() == Role.OWNER) {
            throw new IllegalStateException("일반 사용자애게만 판매가 가능합니다.");
        }

        return findMember;
    }

    private Member getOwner(UserPrincipal user) {
        Optional<Member> owner = memberRepository.findById(user.getId());
        DefaultAssert.isTrue(owner.isPresent(), "사용자가 올바르지 않습니다.");
        Member findMember = owner.get();

        if (findMember.getRole() == Role.CUSTOMER) {
            throw new IllegalStateException("판매 시에는 점주만 로그인 가능합니다.");
        }

        return findMember;
    }

    // 매장을 가져오는 메서드
    private Shop getShop(Long id) {
        return shopRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("해당 shop이 없습니다."));
    }
}
