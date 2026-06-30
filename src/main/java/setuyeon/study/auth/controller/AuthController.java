package setuyeon.study.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import setuyeon.study.auth.dto.LoginRequest;
import setuyeon.study.auth.dto.SignupRequest;
import setuyeon.study.auth.dto.TokenResponse;
import setuyeon.study.auth.service.AuthService;
import setuyeon.study.global.response.ApiResponse;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 1. 회원가입 API
    // 신청서 양식(SignupRequest) -> authService 회원가입 처리 -> MemberId 반환
    @PostMapping("/signup")
    public ApiResponse<Long> signup(@RequestBody SignupRequest request) {
        Long memberId = authService.signup(request.getEmail(), request.getPassword(), request.getName(), request.getRole());

        return ApiResponse.success("회원가입이 완료되었습니다.", memberId);
    }

    // 2. 로그인 API
    // 로그인 양식(LoginRequest) -> authService 로그인 처리 -> Token 반환
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());

        return ApiResponse.success("로그인 성공", new TokenResponse(token));
    }
}
