package umc.parasol.domain.shop.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.parasol.domain.common.Status;
import umc.parasol.domain.history.domain.History;
import umc.parasol.domain.history.domain.Process;
import umc.parasol.domain.history.domain.repository.HistoryRepository;
import umc.parasol.domain.history.dto.HistoryRes;
import umc.parasol.domain.image.domain.Image;
import umc.parasol.domain.image.domain.repository.ImageRepository;
import umc.parasol.domain.image.dto.ImageRes;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.domain.shop.domain.Shop;
import umc.parasol.domain.shop.domain.repository.ShopRepository;
import umc.parasol.domain.shop.dto.*;
import umc.parasol.domain.timetable.domain.TimeTable;
import umc.parasol.domain.timetable.domain.repository.TimeTableRepository;

import umc.parasol.domain.timetable.dto.TimeTableReq;
import umc.parasol.domain.timetable.dto.TimeTableRes;
import umc.parasol.domain.umbrella.application.UmbrellaService;
import umc.parasol.domain.umbrella.domain.Umbrella;
import umc.parasol.domain.umbrella.domain.repository.UmbrellaRepository;
import umc.parasol.global.config.security.token.CurrentUser;
import umc.parasol.global.config.security.token.UserPrincipal;
import umc.parasol.global.payload.ApiResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private final MemberRepository memberRepository;

    private final ShopRepository shopRepository;

    private final ImageRepository imageRepository;

    private final UmbrellaRepository umbrellaRepository;

    private final HistoryRepository historyRepository;

    private final UmbrellaService umbrellaService;
    private final TimeTableRepository timeTableRepository;

    /**
     * 매장 리스트 조회
     * @param userPrincipal api 호출하는 사용자 객체
     */
    public List<ShopListRes> getShopList(UserPrincipal userPrincipal) {

        findValidMember(userPrincipal.getId());
        List<Shop> shopList = shopRepository.findAll();

        return shopList.stream()
                .map(this::createShopListRes)
                .toList();
    }

    /**
     * 특정 매장 조회
     * @param userPrincipal api 호출하는 사용자 객체
     * @param shopId 조회할 매장의 ID
     */
    public ShopRes getShop(UserPrincipal userPrincipal, Long shopId) {

        findValidMember(userPrincipal.getId());
        Shop shop = findValidShop(shopId);

        List<Image> imageList = imageRepository.findAllByShop(shop);
        List<ImageRes> imageResList = imageList.stream()
                .map(this::createImageRes)
                .toList();

        return createShopRes(shop, imageResList);
    }


    // 유효한 사용자인지 체크하는 메서드
    private Member findValidMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 올바르지 않습니다."));
    }

    // 유효한 매장인지 체크하는 메서드
    private Shop findValidShop(Long shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("매장이 올바르지 않습니다."));
    }

    // 사용자가 사장님인지 체크하는 메서드
    private Shop findValidShopForOwner(Member member) {
        return Optional.ofNullable(member.getShop())
                .orElseThrow(() -> new IllegalArgumentException("연결된 매장이 없습니다."));
    }

    // 매장에 있는 모든 우산 리스트를 가져오는 메서드
    public List<Umbrella> getUmbrella(Shop shop) {
        return Optional.ofNullable(umbrellaRepository.findAllByShop(shop))
                .orElseThrow(() -> new IllegalArgumentException("우산이 없습니다."));
    }

    // 매장에 있는 대여 가능한 우산 리스트를 가져오는 메서드
    public List<Umbrella> getAvailableUmbrella(Shop shop) {
        return Optional.ofNullable(umbrellaRepository.findAllByShopAndAvailableIsTrue(shop))
                .orElseThrow(() -> new IllegalArgumentException("대여 가능한 우산이 없습니다."));
    }

    // ShopList 응답을 생성해주는 메서드
    private ShopListRes createShopListRes(Shop shop) {
        List<TimeTableRes> times = timeTableRepository.findAllByShop(shop)
                .stream().map(time -> {
                    return TimeTableRes.dayAndTime(time.getDate(), time.getOpenTime(), time.getEndTime());
                }).toList();
        return ShopListRes.builder()
                .id(shop.getId())
                .shopName(shop.getName())
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .roadNameAddress(shop.getRoadNameAddress())
                .times(times)
                .availableUmbrella(getAvailableUmbrella(shop).size())
                .unavailableUmbrella(getUmbrella(shop).size() - getAvailableUmbrella(shop).size())
                .build();
    }

    // Shop 응답을 생성해주는 메서드
    private ShopRes createShopRes(Shop shop, List<ImageRes> imageResList) {
        String desc = shop.getDescription() != null ? shop.getDescription() : "";
        List<TimeTable> times = timeTableRepository.findAllByShop(shop);
        return ShopRes.builder()
                .id(shop.getId())
                .shopName(shop.getName())
                .desc(desc)
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .roadNameAddress(shop.getRoadNameAddress())
                .times(times.stream().map(time -> {
                    return TimeTableRes.dayAndTime(time.getDate(), time.getOpenTime(), time.getEndTime());
                }).toList())
                .availableUmbrella(getAvailableUmbrella(shop).size())
                .image(imageResList)
                .build();
    }

    // 이미지 응답을 생성해주는 메서드
    private ImageRes createImageRes(Image image) {
        return ImageRes.builder()
                .id(image.getId())
                .url(image.getUrl())
                .build();
    }

    /**
     * 본인 매장 조회
     * @param userPrincipal api 호출하는 사용자 객체
     */
    public ShopRes getShopById(UserPrincipal userPrincipal) {

        Member member = findValidMember(userPrincipal.getId());

        Shop shop = findValidShopForOwner(member);

        List<Image> imageList = imageRepository.findAllByShop(shop);
        List<ImageRes> imageResList = imageList.stream()
                .map(this::createImageRes)
                .toList();

        return createShopRes(shop, imageResList);
    }

    /**
     * 매장 정보 수정
     * @param userPrincipal api 호출하는 사용자 객체
     * @param updateInfoReq update할 정보 객체
     */
    @Transactional
    public ShopRes updateInfo(UserPrincipal userPrincipal, UpdateInfoReq updateInfoReq) {

        Member member = findValidMember(userPrincipal.getId());

        Shop shop = findValidShopForOwner(member);

        shop.updateDescription(updateInfoReq.getDesc());

        List<TimeTable> previousTimeTables = timeTableRepository.findAllByShop(shop);
        for (TimeTable table : previousTimeTables) {
            table.updateStatus(Status.DELETE);
        }

        for (TimeTableReq time : updateInfoReq.getTimes()) {
            TimeTable newTime = TimeTable.from(time.getDay(), time.getOpenTime(), time.getEndTime(), shop);
            timeTableRepository.save(newTime);
        }

        Shop updatedShop = shopRepository.save(shop);


        List<Image> imageList = imageRepository.findAllByShop(updatedShop);
        List<ImageRes> imageResList = imageList.stream()
                .map(this::createImageRes)
                .toList();

        return createShopRes(updatedShop, imageResList);
    }

    /**
     * 우산 대여 처리
     * @param user api 호출하는 사용자 객체 (점주)
     * @param memberId 대여자 객체
     */
    @Transactional
    public ApiResponse rentalUmbrella(@CurrentUser UserPrincipal user, Long memberId) {

        Member owner = findValidMember(user.getId()); //점주
        Shop targetShop = findValidShopForOwner(owner); //점주의 shop

        Member member = findValidMember(memberId); //대여자

        History history = rentalUmbrella(targetShop, member);
        historyRepository.save(history);


        //알림 생성
        Notification notification = notificationService.makeNotification(targetShop, member, NotificationType.RENT_COMPLETED);
        notificationRepository.save(notification);

        HistoryRes record = makeHistoryRes(member, history, null);
        return new ApiResponse(true, record);
    }

    /**
     * 우산 반납 처리
     * @param user api 호출하는 사용자 객체 (점주)
     * @param memberId 대여자 객체
     */
    @Transactional
    public ApiResponse returnUmbrella(@CurrentUser UserPrincipal user, Long memberId) {
        Member owner = findValidMember(user.getId()); //점주
        Shop targetShop = findValidShopForOwner(owner); //점주의 shop

        Member member = findValidMember(memberId); //대여자

        List<History> remainHistoryList = historyRepository.findAllByMemberOrderByCreatedAtDesc(member)
                .stream()
                .filter(history -> history.getProcess() != Process.CLEAR).toList();

        if (remainHistoryList.isEmpty())
            throw new IllegalStateException("처리되지 않은 우산 내역이 없습니다.");

        History targetHistory = remainHistoryList.get(remainHistoryList.size() - 1);
        Umbrella targetUmbrella = targetHistory.getUmbrella();
        targetHistory.updateProcess(Process.CLEAR); // History CLEAR 상태로 변경

        Shop originShop = targetHistory.getFromShop();

        targetUmbrella.updateAvailable(true);

        if (originShop != targetShop) { // 빌렸던 Shop과 다르다면
            targetUmbrella.updateShop(targetShop);
        }
        targetHistory.updateClearedAt(LocalDateTime.now());
        targetHistory.updateEndShop(targetShop);
        HistoryRes record = makeHistoryRes(member, targetHistory, targetShop);


        //알림 생성
        Notification notification = notificationService.makeNotification(targetShop, member, NotificationType.RETURN_COMPLETED);
        notificationRepository.save(notification);

        return new ApiResponse(true, record);
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
