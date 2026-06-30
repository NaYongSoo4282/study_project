package setuyeon.study.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import setuyeon.study.global.exception.BusinessException;
import setuyeon.study.global.exception.ErrorCode;
import setuyeon.study.global.security.JwtTokenProvider;
import setuyeon.study.member.domain.Member;
import setuyeon.study.member.domain.Role;
import setuyeon.study.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    //org.springframework.boot:spring-boot-starter-security 소속
    //SecurityConfig 24line : Bean 등록
    private final JwtTokenProvider jwtTokenProvider;

    // 1. 회원가입
    //controller에서 넘어온 Body
    @Transactional
    public Long signup(String email, String password, String name, Role role) {

        if (memberRepository.findByEmail(email).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }//-> GlobalExceptionHandler


        String encodedPassword = passwordEncoder.encode(password);

        Member member = Member.createMember(name, encodedPassword, email, role);
        //정적 팩토리 메서드

        Member savedMember = memberRepository.save(member);

        return savedMember.getId();
    }

    // 2. 로그인
    @Transactional(readOnly = true)
    public String login(String email, String rawPassword) {
        //토큰 반환 -> String

        //검증1
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_FAILED));

        //검증2
        if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }


        //검증을 통과한 정보 Spring Security용으로 포장
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(member.getEmail(), null,
                        java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + member.getRole().name())));

        //토큰 발급
        return jwtTokenProvider.generateToken(authentication);
    }
}
