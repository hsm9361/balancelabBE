
# BalanceLab BE

**AI 기반 식단 분석 및 건강 예측 웹서비스 - 백엔드 서버 레포지토리**

이 저장소는 BalanceLab 프로젝트의 백엔드 API 서버를 관리합니다.  
FastAPI로 구축된 ML 서버와 연동하며, 사용자 데이터 관리 및 예측 결과 저장 기능을 제공합니다.

---

## 🛠️ 사용 기술

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security
- MariaDB (또는 MySQL)
- Lombok
- Gradle

---

## 🚀 설치 및 실행 방법

### 1. 저장소 클론
```bash
git clone https://github.com/hsm9361/balancelabBE.git
cd balancelabBE
```

### 2. 데이터베이스 설정
- `application.yml` 또는 `application.properties` 파일에 다음 정보를 설정하세요:
```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/balancelab
    username: your_db_username
    password: your_db_password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### 3. 서버 실행
```bash
./gradlew bootRun
```
(또는 IDE에서 `BalancelabBeApplication` 실행)

---

## 📂 프로젝트 구조

```bash
balancelabBE/
├── src/main/java/
│   ├── com/ai/balancelab_be/
│       ├── domain/         # 도메인별 Entity, Repository, Service
│       ├── global/         # 공통 모듈 (Exception, Security 등)
│       ├── config/         # 설정 파일
│       └── BalancelabBeApplication.java
├── src/main/resources/
│   ├── application.yml     # 환경 설정 파일
├── build.gradle            # 빌드 설정
└── README.md
```

---

## 📌 주요 기능

- 사용자 회원가입, 로그인 기능 (Spring Security 기반)
- 식단 기록 및 조회 API
- 식단 분석 결과 저장 및 조회 API
- 건강 예측 모델 서버 연동 (FastAPI 서버와 통신)

---

## 🔗 관련 저장소

- [BalanceLab FE (프론트엔드)](https://github.com/hsm9361/balancelabFE)
- [BalanceLab ML (모델 서버)](https://github.com/hsm9361/balancelabML)

---

## 📢 주의사항

- 데이터베이스는 **MariaDB** 또는 **MySQL** 사용을 권장합니다.
- `.env` 또는 `application.yml` 파일에 민감한 정보(DB 비밀번호 등)는 깃허브에 올리지 않도록 주의하세요.

---

# ✨ About

BalanceLab 프로젝트는 AI 기술을 활용하여 식습관 개선과 건강 관리를 지원하는 서비스를 목표로 합니다.
