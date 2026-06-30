package setuyeon.study.feedback.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import setuyeon.study.feedback.dto.FeedbackCreateRequest;
import setuyeon.study.feedback.dto.FeedbackResponse;
import setuyeon.study.feedback.service.FeedbackService;
import setuyeon.study.global.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/events/{eventId}/feedbacks")
    public ApiResponse<FeedbackResponse> createFeedback(
            @PathVariable Long eventId,
            @RequestBody FeedbackCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ApiResponse.success("피드백 작성 완료",
                feedbackService.createFeedback(eventId, request, userDetails.getUsername()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/events/{eventId}/feedbacks")
    public ApiResponse<List<FeedbackResponse>> getFeedbacks(@PathVariable Long eventId) {
        return ApiResponse.success("행사 피드백 목록 조회 완료",
                feedbackService.getFeedbacks(eventId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/feedbacks")
    public ApiResponse<List<FeedbackResponse>> getAllFeedbacks() {
        return ApiResponse.success("전체 피드백 목록 조회 완료",
                feedbackService.getAllFeedbacks());
    }
}
