# Study Event Platform

동아리 또는 스터디 조직에서 행사를 생성하고, 회원 신청/승인/출석/피드백을 관리할 수 있는 학습용 행사 운영 플랫폼입니다.

Spring Boot 기반 백엔드 API와 정적 프론트엔드로 구성되어 있으며, 회원과 관리자의 역할을 나누어 행사 운영 흐름을 구현했습니다.

## 주요 기능

### 회원 기능

- 회원가입 및 로그인
- 행사 목록 조회
- 행사 신청
- 내 신청 상태 조회
- 신청 취소
- 승인된 행사 출석 체크
- 출석한 완료 행사에 피드백 작성

### 관리자 기능

- 관리자 회원가입 및 로그인
- 회원 목록 조회
- 행사 생성, 수정, 삭제
- 행사 상태 변경
- 행사 신청자 목록 조회
- 신청 승인/반려/취소 처리
- 행사 출석 코드 발급
- 출석 현황 조회
- 행사별/전체 피드백 조회
- 운영 대시보드 조회

## 기술 스택

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT
- HTML / CSS / JavaScript(Frontend)

## 설계 의도

### 1. 보안 처리

보안은 두 단계로 나누어 처리했습니다.

첫 번째는 `SecurityConfig`에서 URL 기반 접근 제어를 적용한 것입니다.  
예를 들어 `/api/v1/admin/**` 경로는 관리자 권한을 가진 사용자만 접근할 수 있도록 설정했습니다.

두 번째는 각 컨트롤러 메서드에 `@PreAuthorize`를 적용하여 메서드 단위 권한 검사를 추가한 것입니다.

### 2. 예외 처리

도메인별 비즈니스 상황에서 발생하는 예외는 `BusinessException`으로 처리했습니다.

예외 종류는 `ErrorCode` Enum에 정의하여 관리했고, `GlobalExceptionHandler`에서 이를 받아 `ErrorResponse` 형태로 변환해 클라이언트에 응답하도록 구성했습니다.

이를 통해 예외 응답 형식을 통일하고, 각 도메인에서 어떤 문제가 발생했는지 명확하게 표현하려고 했습니다.

예시 상황:

- 이미 가입된 이메일
- 존재하지 않는 행사
- 모집 중이 아닌 행사 신청
- 정원 초과 승인
- 승인되지 않은 신청자의 출석
- 출석하지 않은 회원의 피드백 작성
- 중복 피드백 작성

### 3. API 응답 규칙

성공 응답은 `ApiResponse<T>`로 감싸서 반환하도록 했습니다.

응답에는 공통적으로 다음 정보를 포함합니다.

- `status`
- `message`
- `data`

이를 통해 프론트엔드에서 API 응답을 일관된 방식으로 처리할 수 있도록 했습니다.

### 4. 엔드포인트 규칙

API 엔드포인트는 `/api/v1`을 기준으로 구성했습니다.

관리자 기능은 `/api/v1/admin/**` 경로를 사용하고, 일반 회원도 사용할 수 있는 기능은 `/api/v1/events/**`, `/api/v1/members/**` 형태로 분리했습니다.

예시:

- `POST /api/v1/auth/signup`
- `POST /api/v1/auth/login`
- `GET /api/v1/events`
- `POST /api/v1/events/{eventId}/applications`
- `PATCH /api/v1/events/{eventId}/applications/{applicationId}/status`
- `POST /api/v1/admin/events`
- `GET /api/v1/admin/dashboard`

### 5. 도메인 중심 구조

프로젝트는 도메인별 패키지 구조를 기준으로 나누었습니다.

- `auth`
- `member`
- `event`
- `application`
- `attendance`
- `feedback`
- `dashboard`
- `global`

각 도메인은 controller, service, repository, domain, dto를 기준으로 역할을 분리했습니다.

### 6. 비즈니스 규칙

다음과 같은 규칙을 적용했습니다.

- 행사는 `DRAFT`, `OPEN`, `CLOSED`, `COMPLETED`, `CANCELED` 상태를 가집니다.
- 회원은 `OPEN` 상태의 행사에 신청할 수 있습니다.
- 신청은 여러 명 받을 수 있지만, 관리자가 승인할 때 정원을 초과할 수 없습니다.
- 출석은 `APPROVED` 상태의 신청자만 가능합니다.
- 피드백은 `COMPLETED` 상태의 행사에 대해 작성할 수 있습니다.
- 단, 실제 출석 기록이 있는 회원만 피드백을 작성할 수 있습니다.
- 한 회원은 한 행사에 대해 피드백을 한 번만 작성할 수 있습니다.

### 7. 데이터베이스

DB는 PostgreSQL을 사용했습니다.

초기에는 매 실행마다 테이블을 새로 만들기 위해 `spring.jpa.hibernate.ddl-auto=create`를 사용했습니다.  
이후 데이터를 유지하면서 테스트하기 위해 현재는 `spring.jpa.hibernate.ddl-auto=update`로 변경했습니다.

DB 접속 정보는 GitHub에 직접 올리지 않기 위해 환경 변수로 분리했습니다.

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

### 8. 프론트엔드

프론트엔드 코드는 Codex를 활용하여 작성했습니다.

관리자와 일반회원 화면을 나누고, 각 도메인별 기능을 화면에서 확인할 수 있도록 구성했습니다.

### 9. JWT 비밀키

JWT 비밀키는 학습 과정에서 Codex가 작성한 값을 참고했습니다.

GitHub에 올릴 때는 실제 비밀키를 코드에 직접 포함하지 않기 위해 `JWT_SECRET` 환경 변수로 분리할 수 있도록 수정했습니다.

로컬 실행 시에는 `JWT_SECRET`에 Base64로 인코딩된 충분히 긴 값을 넣어야 합니다.

## 학습 관점

이 프로젝트는 AI 도구를 활용했지만, 단순히 코드를 생성하는 것이 아니라 제가 구조와 흐름을 이해하는 것을 목표로 진행했습니다.

특히 인증, 권한 분리, 예외 처리, API 응답 규칙, 도메인 간 관계, 신청-승인-출석-피드백으로 이어지는 비즈니스 흐름을 직접 설명할 수 있도록 정리하는 데 초점을 두었습니다.

자세한 기능 명세와 도메인 관계는 `docs/project-spec.md`에 정리했습니다.
