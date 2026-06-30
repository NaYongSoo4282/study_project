package setuyeon.study.auth.dto;

import lombok.Getter;
import setuyeon.study.member.domain.Role;

@Getter
public class SignupRequest {
    private String email;
    private String password;
    private String name;
    private Role role;
}
