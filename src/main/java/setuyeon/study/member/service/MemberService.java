package setuyeon.study.member.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import setuyeon.study.global.exception.BusinessException;
import setuyeon.study.global.exception.ErrorCode;
import setuyeon.study.member.domain.Member;
import setuyeon.study.member.dto.MemberResponse;
import setuyeon.study.member.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public List<MemberResponse> getMembers(){

        List<MemberResponse> members = memberRepository.findAll().stream()
                .map(MemberResponse::new).toList();

        return members;
    }

    public MemberResponse getMyProfile(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        return new MemberResponse(member);
    }

}
