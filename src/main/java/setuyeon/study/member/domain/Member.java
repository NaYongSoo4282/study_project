package setuyeon.study.member.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Member {

    /*
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_generator")
    @SequenceGenerator(
            name = "member_seq_generator",
            sequenceName = "member_seq",
            allocationSize = 1
    )
    PostgreSQL일 경우 이런 방식을 사용할 수 있다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    protected Member() {
    }

    //정적 팩토리 메서드
    public static Member createMember(String name, String password, String email, Role role){
        Member member = new Member();
        member.name = name;
        member.password = password;
        member.email = email;
        member.role = role == null ? Role.MEMBER : role;
        return member;
    }
}
