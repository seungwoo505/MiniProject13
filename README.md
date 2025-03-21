# LG U+ UREKA 프론트엔드 비대면 13조(비밀 창고)

## 팀원

<table>
    <tr>
    <td height="140px" align="center"> <a href="https://github.com/KimJunSeo289"><img src="https://avatars.githubusercontent.com/u/200718064?v=4" width="140px" /><br/>⚽️ 김준서</a></td>
    <td height="140px" align="center"> <a href="https://github.com/seungwoo505"><img src="https://avatars.githubusercontent.com/u/51819005?v=4" width="140px" /><br/>🐰 이승우</a></td>
    </tr>
</table>

## 기술 스택

- 프론트엔드
	- HTML
	- CSS
	- JavaScript
	- Bootstrap
- 백엔드
	- Spring(STS)
	- MariaDB

## 개발 기간

- 2025.03.13 ~ 2025.03.21

## 시연 영상

[![Video Label](http://img.youtube.com/vi/XjQ0Ka2Zd_E/0.jpg)](https://www.youtube.com/watch?v=XjQ0Ka2Zd_E=0s)

## 기능 요약

- 회원가입 및 로그인 기능
	- 입력 조건 검증 (글자 수, 특수문자 포함)
	- 로그인 5회 실패 시 계정 잠금
	- 회원가입 시 salt 발급 후 비밀번호 암호화
	- 로그인 시 토큰 발급 및 사용자 인증
	- 모든 활동은 토큰과 사용자 아이디를 비교하여 인증을 거쳐야만 수행 가능하며, 활동 시마다 새로운 토큰 발급
- 파일 관리 기능
	- 최대 5개 파일, 총 20MB까지 업로드 가능
	- 업로드 시 바이러스 체크를 하여 보안 강화
	- 파일 이름과 내용 암호화하여 DB에 저장
	- 파일 다운로드 기능
	- 다운로드할 때 업로드 당시 데이터와 일치하는지 확인하여 
	- 공유 기능: 전체 사용자 또는 특정 사용자 대상으로 공유 링크 생성 가능
	- 파일 탐색 기능 제공