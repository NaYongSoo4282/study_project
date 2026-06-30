package setuyeon.study.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 🚀 OncePerRequestFilter: 클라이언트의 한 번의 요청 당 딱 한 번만 실행됨을 보장하는 스프링의 근본 필터입니다.
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 헤더에서 토큰을 추출합니다. (밑에 있는 resolveToken 메서드 사용)
        String token = resolveToken(request);

        // 2. 토큰이 비어있지 않고, 위조되지 않았다면(validateToken)
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // 3. 토큰을 찢어서 안에 있는 권한 정보를 가져옵니다. (DB 조회 안 함!)
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            // 4. 스프링 시큐리티의 핵심 임시 저장소(SecurityContext)에 VIP 입장권을 걸어둡니다.
            // 이걸 걸어둬야 컨트롤러에서 @PreAuthorize("hasRole('ADMIN')") 같은 어노테이션이 작동합니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 💀 최악의 시나리오 방어: 토큰이 없거나 가짜여도 일단 다음 필터로 넘겨줘야 합니다.
        // 여기서 안 넘겨주면 서버가 하얗게 멈춥니다(응답 지연).
        // 권한이 없어서 튕겨내는 역할은 이 필터가 아니라, 뒷단에 있는 시큐리티 인가(Authorization) 필터가 알아서 해줍니다.
        filterChain.doFilter(request, response);
    }

    // 헤더에서 "Bearer eyJhb..." 형태의 토큰 앞부분(Bearer )을 잘라내고 순수 토큰만 가져오는 핀셋 로직
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}