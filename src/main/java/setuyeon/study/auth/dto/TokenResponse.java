package setuyeon.study.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
}
//Dto 반환 규칙에 따라 사용
//기능 상 없어도 작동 가능