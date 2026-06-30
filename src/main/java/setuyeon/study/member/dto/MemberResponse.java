package setuyeon.study.member.dto;

import lombok.Getter;
import setuyeon.study.member.domain.Member;
import setuyeon.study.member.domain.Role;

@Getter
public class MemberResponse {
    String name;
    String email;
    Long id;
    Role role;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.role = member.getRole();
    }
}
