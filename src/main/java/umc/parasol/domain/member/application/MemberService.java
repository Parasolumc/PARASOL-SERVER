package umc.parasol.domain.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.parasol.domain.auth.application.AuthSignService;
import umc.parasol.domain.auth.dto.RefreshTokenReq;
import umc.parasol.domain.common.Status;
import umc.parasol.domain.history.domain.History;
import umc.parasol.domain.history.domain.Process;
import umc.parasol.domain.history.domain.repository.HistoryRepository;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.Role;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.domain.member.dto.GetCustomerKeyRes;
import umc.parasol.domain.member.dto.UpdatePwReq;
import umc.parasol.domain.shop.domain.Shop;
import umc.parasol.domain.umbrella.domain.Umbrella;
import umc.parasol.domain.umbrella.domain.repository.UmbrellaRepository;
import umc.parasol.global.DefaultAssert;
import umc.parasol.global.config.security.token.UserPrincipal;
import umc.parasol.global.payload.ApiResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthSignService authSignService;
    private final HistoryRepository historyRepository;
    private final UmbrellaRepository umbrellaRepository;

    // 비밀번호 변경
    @Transactional
    public ApiResponse updatePassword(UpdatePwReq updatePwReq, UserPrincipal user) {
        // 1. 현재 비밀번호, 새 비밀번호, 새 비밀번호 재입력 ok -> 변경
        // 2. 인증 후 비밀번호 재설정
        String oldPw = updatePwReq.getOldPw();
        validateOriginPassword(oldPw, user.getPassword());

        String newPw = updatePwReq.getNewPw();
        
        if (oldPw.equals(newPw))
            throw new IllegalStateException("변경하려는 비밀번호가 같습니다.");

        String reNewPw = updatePwReq.getReNewPw();

        if (!newPw.equals(reNewPw))
            throw new IllegalStateException("새로 입력한 비밀번호가 서로 일치하지 않습니다.");

        Member findMember = memberRepository.findById(user.getId()).orElseThrow(
                () -> new IllegalStateException("해당 member가 없습니다.")
        );

        findMember.updatePassword(passwordEncoder.encode(newPw));

        authSignService.signOut(new RefreshTokenReq(updatePwReq.getRefreshToken())); // 로그아웃 처리

        return ApiResponse.builder()
                .check(true)
                .information("비밀번호 변경 완료")
                .build();
    }

    // 회원 탈퇴
    @Transactional
    public ApiResponse deleteMember(UserPrincipal member) {
        Member targetMember = findMemberById(member.getId());
        // 일반 회원일 시 : 미반납 우산이 있거나 미납된 연체료가 있는 회원은 탈퇴가 불가
        // 미반납 우산과 미납 연체료는 모두 History로부터 조회
        validateIsCustomerAndDeleteMember(targetMember);

        // 점주일 시
        validateIsOwnerAndDeleteMember(targetMember);

        targetMember.updateStatus(Status.DELETE);
        return new ApiResponse(true, "탈퇴 완료");
    }

    // CUSTOMER_KEY 발급 (toss payments)
    public GetCustomerKeyRes getCustomerKey(UserPrincipal userPrincipal) {
        Member member = findMemberById(userPrincipal.getId());
        return GetCustomerKeyRes.builder().customerKey(member.getTossCustomerKey()).build();
    }

    @Transactional
    void validateIsOwnerAndDeleteMember(Member targetMember) {
        if (isOwner(targetMember)) {
            // 해당 Shop으로 등록된 사용중인 우산이 없어야 함
            Shop targetShop = targetMember.getShop();
            List<Umbrella> umbrellas = umbrellaRepository.findAllByShop(targetShop);
            validateIsEmptyUseStateUmbrella(umbrellas);
            umbrellas.forEach(umbrella -> umbrella.updateStatus(Status.DELETE));
            targetShop.updateStatus(Status.DELETE);
        }
    }

    @Transactional
    void validateIsCustomerAndDeleteMember(Member targetMember) {
        if (isCustomer(targetMember)) {
            List<History> histories = historyRepository.findAllByMember(targetMember);
            validateIsEmptyUseStateHistory(histories);
            validateIsEmptyDelayStateHistory(histories);
            histories.forEach(history -> history.updateStatus(Status.DELETE));
        }
    }

    private static void validateIsEmptyUseStateUmbrella(List<Umbrella> umbrellas) {
        List<Umbrella> unAvailableList = umbrellas.stream()
                .filter(umbrella -> !umbrella.isAvailable()).toList();
        if (!unAvailableList.isEmpty())
            throw new IllegalStateException("사용 중인 우산이 있습니다.");
    }

    private static void validateIsEmptyDelayStateHistory(List<History> histories) {
        List<History> delayHistory = histories.stream()
                .filter(history -> history.getProcess() == Process.DELAY).toList();
        if (!delayHistory.isEmpty())
            throw new IllegalStateException("미납된 이용 내역이 있습니다.");
    }

    private static void validateIsEmptyUseStateHistory(List<History> histories) {
        List<History> usingHistory = histories.stream()
                .filter(history -> history.getProcess() == Process.USE).toList();
        if (!usingHistory.isEmpty())
            throw new IllegalStateException("아직 처리되지 않은 이용 내역이 있습니다.");
    }

    private void validateOriginPassword(String checkPassword, String password) {
        if (!BCrypt.checkpw(checkPassword, password))
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
    }

    private Member findMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("해당 member가 없습니다.")
        );
    }

    private boolean isCustomer(Member member) {
        return member.getRole() == Role.CUSTOMER;
    }

    private boolean isOwner(Member member) {
        return member.getRole() == Role.OWNER;
    }
}
