package umc.parasol.domain.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.parasol.domain.auth.application.AuthSignService;
import umc.parasol.domain.auth.dto.RefreshTokenReq;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.domain.member.dto.UpdatePwReq;
import umc.parasol.global.config.security.token.UserPrincipal;
import umc.parasol.global.payload.ApiResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthSignService authSignService;

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
    public void deleteMember(UserPrincipal userPrincipal) {
        // 미반납 우산이 있거나, 미납된 연체료가 있는 회원은 탈퇴가 불가 (예외처리)
    }

    private void validateOriginPassword(String checkPassword, String password) {
        if (!BCrypt.checkpw(checkPassword, password))
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
    }
}
