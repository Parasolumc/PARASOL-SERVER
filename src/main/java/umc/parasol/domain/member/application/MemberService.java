package umc.parasol.domain.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.domain.member.dto.UpdatePwReq;
import umc.parasol.global.config.security.token.UserPrincipal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    // 비밀번호 변경
    public void updatePassword(UpdatePwReq updatePwReq) {
        // 1. 현재 비밀번호, 새 비밀번호, 새 비밀번호 재입력 ok -> 변경
        // 2. 인증 후 비밀번호 재설정


    }

    // 회원 탈퇴
    public void deleteMember(UserPrincipal userPrincipal) {
        // 미반납 우산이 있거나, 미납된 연체료가 있는 회원은 탈퇴가 불가 (예외처리)
    }
}
