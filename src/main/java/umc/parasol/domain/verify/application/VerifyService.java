package umc.parasol.domain.verify.application;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.domain.verify.domain.Verify;
import umc.parasol.domain.verify.domain.repository.VerifyRepository;
import umc.parasol.domain.verify.dto.CheckReq;
import umc.parasol.domain.verify.dto.VerifyReq;
import umc.parasol.global.payload.ApiResponse;

import java.time.LocalDateTime;
import java.util.Random;

import static net.nurigo.sdk.NurigoApp.INSTANCE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VerifyService {

    private final MemberRepository memberRepository;

    @Value("${message.api-key}")
    private String MessageApiKey;

    @Value("${message.secret-key}")
    private String MessageSecretKey;

    @Value("${message.domain}")
    private String MessageDomain;

    @Value("${message.host}")
    private String MessageHostNumber;
    private final VerifyRepository verifyRepository;

    // 문자 인증
    @Transactional
    public ApiResponse verify(VerifyReq verifyReq) {
        int code = generateVerifyCode();
        Member findMember = findMemberByEmail(verifyReq.getEmail());
        updateMemberNumberAndName(verifyReq, findMember);
        Verify newVerify = createVerify(code, findMember);
        verifyRepository.save(newVerify);
        sendMessage(verifyReq, code);

        return new ApiResponse(true, newVerify);
    }

    private static Verify createVerify(int code, Member findMember) {
        return Verify.builder()
                .code(code)
                .member(findMember)
                .build();
    }

    private void sendMessage(VerifyReq verifyReq, int code) {
        DefaultMessageService messageService = INSTANCE.initialize(MessageApiKey, MessageSecretKey, MessageDomain);
        Message newMessage = generateVerifyMessage(verifyReq.getPhoneNumber(), code);
        messageService.sendOne(new SingleMessageSendingRequest(newMessage));
    }

    private static void updateMemberNumberAndName(VerifyReq verifyReq, Member findMember) {
        findMember.updatePhoneNumber(verifyReq.getPhoneNumber());
        findMember.updateName(verifyReq.getName());
    }

    // 문자 인증 확인
    @Transactional
    public ApiResponse check(CheckReq checkReq) {
        Verify findVerify = verifyRepository.findByCode(checkReq.getCode())
                .orElseThrow(() -> new IllegalArgumentException("코드가 일치하지 않습니다."));

        LocalDateTime requestTime = LocalDateTime.now();
        if (findVerify.checkExpiration(requestTime)) {
            verifyRepository.delete(findVerify);
            throw new IllegalStateException("인증 시간이 만료되었습니다.");
        }

        Member existMember = findMemberByPhoneNumber(checkReq.getPhoneNumber());
        Member findMember = findVerify.getMember();
        if (!existMember.getId().equals(findMember.getId()))
            throw new IllegalStateException("유저 정보가 일치하지 않습니다.");

        if (findMember.getIsVerified())
            throw new IllegalStateException("이미 인증되었습니다.");

        findMember.updateIsVerified(true);

        verifyRepository.delete(findVerify);

        return new ApiResponse(true, "인증 완료");
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("유저 정보를 찾을 수 없습니다."));
    }

    private Member findMemberByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("유저 정보를 찾을 수 없습니다."));
    }

    // 문자 인증 코드 생성
    private int generateVerifyCode() {
        return new Random().nextInt(999999 - 10000 + 1) + 100000;
    }

    // 문자 생성
    private Message generateVerifyMessage(String number, int code) {
        Message newMessage = new Message();
        newMessage.setFrom(MessageHostNumber);
        newMessage.setTo(number);
        newMessage.setText("[Parasol] 인증 번호는 " + code + " 입니다.");

        return newMessage;
    }
}
