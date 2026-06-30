package setuyeon.study.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 컨트롤러에서 @PreAuthorize 사용을 허용하는 마법의 어노테이션
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. 🚀 거대한 고속도로 톨게이트 교통정리 (가장 중요)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // [1] JWT를 쓰므로 CSRF 방어와 세션(수첩)은 완전히 꺼버립니다. (Stateless)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // [2] 도로별 출입 허가증 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/", "/index.html", "/styles.css", "/app.js", "/favicon.ico").permitAll()
                        // 회원가입과 로그인은 여권이 없는 상태이므로 무조건 프리패스 (permitAll)
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // 관리자 전용 경로는 무조건 ADMIN 권한이 있어야 통과 (hasRole)
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        // 그 외의 모든 요청(행사 조회, 신청 등)은 여권(토큰)만 있으면 통과 (authenticated)
                        .anyRequest().authenticated()
                )

                // [3] 🚀 우리가 만든 검문소를 실제 톨게이트에 배치하는 핵심 코드!
                // 스프링의 기본 검문소(UsernamePasswordAuthenticationFilter)가 작동하기 "직전"에
                // 우리의 JwtAuthenticationFilter를 끼워 넣습니다.
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
