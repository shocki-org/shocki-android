# Shocki
> 쇼키
<img width="1930" alt="25" src="https://github.com/user-attachments/assets/6f3db380-490a-4b99-aa32-568a4fb4e9c5">

## 프로젝트 소개

- 플랫폼 : 안드로이드/모바일
- 제작 인원 : 5 인 (팀 구성 ― 안드로이드 1명, 백엔드 1명, 디자이너 1명, 블록체인 1명, 기획자 1명)
- 제작 기간 : 82 일 (2024.07.06 ~ 2024.09.26)
- 사용 기술 : Kotlin, XML, MetaMask SDK, FCM, Toss Payments SDK, ViewBinding, Glide, Retrofit2, ViewPager2, Google OAuth, KakaO OAuth

'쇼키'는 창의적인 아이디어를 가진 창작자들이 자신의 아이디어를 실현할 수 있도록 지원하는 블록체인 기반의 크라우드 펀딩 플랫폼으로
NFT와 F-NFT를 이용해 기존 플랫폼들이 가지고 있던 수수료, 리워드 등의 문제를 효과적으로 해결하며, 새롭고 신선한 투자의 방식을 제시하는
서비스입니다

## 사용 예제

### 스샷

<div style="text-align: left;">    
    <img src="https://github.com/user-attachments/assets/6a0daeb4-35be-4752-b4a2-e1606328b00e"  width="200" height="400"/>
    <img src="https://github.com/user-attachments/assets/c51c22db-35a8-4f19-b107-7d33f9efe47b"  width="200" height="400"/>
    <img src="https://github.com/user-attachments/assets/b183b13b-ca5e-4cae-aa5f-09e9237fe69c"  width="200" height="400"/>
</div>

### 시연 영상

https://github.com/user-attachments/assets/cc460d38-2732-4c76-9940-fb1940677768

## 구현 기능

- 로그인 & 회원가입
    - 구글 OAuth
    - 카카오 OAuth
    - 전화번호 로그인 (문자 인증)
- 지갑 연결
    - MetaMask 지갑 연결
- 홈
    - 펀딩 상품 및 판매 상품 배너
    - FCM 알림
- 알림
    - 알림 리스트
- 검색
    - 펀딩 상품 및 판매 상품 검색 (Debounce)
- 마켓
    - 판매 상품 라스트
- 배송
    - 구매 상품 배송 상태 조회
    - 상품 신고
- 상품 상세 페이지 & 구매 페이지
    - 상품 구매하기
    - 상품 상세 정보
    - 주소 검색
- 펀딩 상세 페이지
    - 찜
    - 펀딩 가격 변경 그래프
    - 펀딩 상품 상세 정보
    - QnA 목록 및 QnA 작성
    - 토큰 구매
    - 토큰 판매
- 마이페이지
    - 자산 조회 (평가 자산, 보유 자산, 정산 예정 자산)
    - 크레딧 충전 하기 (토스 페이먼츠)
    - 찜 상품 리스트
    - 보유 토큰 상품 리스트
    - 정산 예정 상품 리스트
    - 계정 로그아웃
    - 계정 탈퇴

<br>

## 배운 점 & 아쉬운 점 & 이슈

배운 점 및 아쉬운 점, 이슈 등은 블로그 회로록을 정리하였습니다. 관심 있으시다면 해당 [포스트]()를 확인해주세요.

## 느낀 점

Shocki 프로젝트에서 Kotlin을 사용한 Android 앱 개발 총괄 및 구글 플레이 스토어 출시를 담당하였으며 개발하였습니다. 해당 앱을 통해 처음으로 MetaMask SDK, TossPayments SDK 등 다양한 SDK 사용 및 토스트틱한 UI 구현 경험을 얻을 수 있었습니다. 또한 'STA+C 2024'에서 장려상을 수상하는 결과를 얻을 수 있었습니다.
