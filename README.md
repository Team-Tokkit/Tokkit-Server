<p align="center">
  <img src="https://github.com/user-attachments/assets/cea3445b-9f02-44b9-925c-dff472d896ae" width="180" alt="Tokkit Logo"/>
</p>
<p align="center"><i>“토큰이 있으면, 토킷이 있다.”</i></p>




# 🐰 Tokkit-Server 
**Tokkit**은 우리은행 예금 토큰 기반의 **CBDC 전자지갑 서비스**입니다.  
이 저장소는 **백엔드(Spring Boot)** API 서버로, 전자지갑, 바우처, 결제 및 사용자/관리자 기능을 제공합니다.

<br>

## :technologist: Team
|                                                               **노영재**                                                               |                                                               **이승준**                                                               |                                                               **조윤주**                                                               |                                                               **이서연**                                                               |                                                               **이정민**                                                               |
| :------------------------------------------------------------------------------------------------------------------------------------: | :------------------------------------------------------------------------------------------------------------------------------------: | :------------------------------------------------------------------------------------------------------------------------------------: | :------------------------------------------------------------------------------------------------------------------------------------: | :------------------------------------------------------------------------------------------------------------------------------------: |
| [<img src="https://avatars.githubusercontent.com/u/146312456?v=4" height=100 width=100> <br/> @YoungjaeRo](https://github.com/YoungjaeRo) | [<img src="https://avatars.githubusercontent.com/u/105282117?v=4" height=100 width=100> <br/> @sengjun0624](https://github.com/sengjun0624)| [<img src="https://avatars.githubusercontent.com/u/155442976?v=4" height=100 width=100> <br/> @iamyuunzo](https://github.com/iamyuunzo) | [<img src="https://avatars.githubusercontent.com/u/90055686?v=4" height=100 width=100> <br/> @noeyoes](https://github.com/noeyoes) | [<img src="https://avatars.githubusercontent.com/u/152269806?v=4" height=100 width=100> <br/> @jeongmin07262](https://github.com/jeongmin07262)

<br>

## 🌿 브랜치 규칙

- `main` : 실제 배포 브랜치
- `dev` : 개발 통합 브랜치
- `feature/BE-12-기능설명` : 기능 개발 브랜치 (서버 기준 BE-XX)
- `fix/BE-21-버그수정` : 버그 수정 브랜치
- `hotfix/BE-99-긴급수정` : 긴급 핫픽스

<br>

## 🧾 커밋 메시지 규칙

```bash
[태그] #이슈번호 - 작업 내용 (영문 or 한글 자유)

예:
[FEAT] #12 - 로그인 화면 UI 구현
[FIX] #21 - 바우처 미표시 버그 수정
```

<br>

## ✅ 백엔드 주요 커밋 태그
| 태그 | 의미 |
|------|------|
| `FEAT` | 기능 개발 (API, 서비스 로직 등) |
| `FIX` | 버그 수정 (예외 처리, 동작 오류 등) |
| `REFACTOR` | 코드 리팩토링 (로직 변경 없이 구조 개선) |
| `TEST` | 테스트 코드 작성 및 수정 |
| `CHORE` | 설정, 빌드, 의존성 등 프로젝트 구성 작업 |
| `DOCS` | README, API 명세, 주석 등 문서 관련 작업 |
| `CONFIG` | 환경설정 파일 작업 (YAML, properties 등) |
| `SECURITY` | 인증, 인가 등 보안 관련 작업 |
| `PERF` | 성능 개선 (쿼리 최적화, 캐싱 등) |
| `DB` | 데이터베이스 작업 (DDL, 쿼리 수정 등) |

<br>

## 🏷️ 백엔드 라벨 체계
| 라벨 | 설명 |
|------|--------|
| `FEAT` | 기능 개발 관련 PR/이슈 |
| `FIX` | 버그 수정 관련 |
| `REFACTOR` | 리팩토링 관련 (로직 변경 없음) |
| `TEST` | 테스트 코드 관련 작업 |
| `CHORE` | 빌드, 설정, 의존성 등 기타 작업 |
| `DOCS` | 문서 작업 (README, 주석 등) |
| `CONFIG` | 환경설정 관련 작업 |
| `SECURITY` | 보안 관련 수정 (JWT, 인증 등) |
| `PERF` | 성능 개선 (쿼리 최적화, 캐싱 등) |
| `DB` | DB 작업 (DDL, 쿼리 수정 등) |

