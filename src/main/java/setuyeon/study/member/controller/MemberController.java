package setuyeon.study.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import setuyeon.study.global.response.ApiResponse;
import setuyeon.study.member.dto.MemberResponse;
import setuyeon.study.member.service.MemberService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class MemberController {

    private final MemberService memberService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/members")
    public ApiResponse<List<MemberResponse>> getMembers(){

        return ApiResponse.success("전체 회원 조회 완료", memberService.getMembers());
    }

    @GetMapping("/members/me")
    public ApiResponse<MemberResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {

        return ApiResponse.success("내 정보 조회 완료", memberService.getMyProfile(userDetails.getUsername()));
    }
}
