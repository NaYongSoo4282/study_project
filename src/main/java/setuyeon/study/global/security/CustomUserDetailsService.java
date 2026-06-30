//package setuyeon.study.global.security;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import setuyeon.study.member.domain.Member;
//import setuyeon.study.member.repository.MemberRepository;
//
//import java.util.Collections;
//
//@Service
//@RequiredArgsConstructor
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final MemberRepository memberRepository;
//
//    // 🚀 시큐리티가 "이 이메일 가진 사람 정보 좀 가져와봐!" 하고 명령을 내리는 유일한 창구입니다.
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//
//        // 1. 형님의 리포지토리를 써서 진짜 DB에서 Member를 꺼냅니다.
//        Member member = memberRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 유저가 없습니다."));
//
//        // 2. 꺼낸 Member의 정보를 시큐리티의 규격 봉투인 'User' 객체(UserDetails의 구현체)에 담습니다.
//        // 이때 Role 정보 앞에는 반드시 "ROLE_" 이라는 접두사를 붙여야 시큐리티가 권한으로 인식합니다.
//        return new User(
//                member.getEmail(),     // 아이디 역할 (우리는 이메일 사용)
//                member.getPassword(),  // 암호화된 비밀번호
//                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + member.getRole().name())) // 권한
//        );
//    }
//}