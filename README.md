# Getting Started

## 1. Poject 개발 환경
* JDK - 17
* Springboot - 3.3.0
* Gradle - 8.8
* IDE - Intellij


## 2. Local 환경 실행 가이드
### IntelliJ IDE
```
Gradle - Tasks - verification - test
Gradle - Tasks - application - bootRun
```
### bash
```shell
sudo chmod 775 ./gradlew 
./gradlew clean test  
./gradlew clean bootRun
```
### Local 환경 실행시 H2 콘솔 접속 
* [http://localhost:8080/h2-console 접속](http://localhost:8080/h2-console)
* JDBC URL: jdbc:h2:mem:testdb;MODE=MySQL
* User Name: sa
* Password:

### TC 가이드  환경 실행시 H2 콘솔 접속
* [http://localhost:8080/h2-console 접속](http://localhost:8080/h2-console)
* JDBC URL: jdbc:h2:mem:testdb;MODE=MySQL
* User Name: sa
* Password:

## 3. Client 인증 처리
### 3.1 login 요청
```
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
    "email" :"test@test.com",
    "password" :"1234"
}
```
### 3.2 response body json 의 data 에서 accessToken 취득
```
HTTP/1.1 200 OK
Content-Type: application/json

{
  "type": "OK",
  "message": "ok",
  "data": {
    "id": 1,
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwibmFtZSI6InRlc3QiLCJpYXQiOjE3MTkyNjcwMTksImV4cCI6MTcxOTI3MDYxOX0.s1KEEtQdFnnzljP-LiznrhF8HfnRHDqnzr4JtW9E55s"
  }
}
```
### 3.3 인증이 필요한 Api 에 Header 에 accessToken 추가하여 요청
```curl
//sample request 
POST http:///localhost:8080/api/v1/wallet/deposit
Authorization: Bearer <accessToken>

{
    "eventType" : "DEPOSIT",
    "amount" : 60000000
}

//sample response
HTTP/1.1 200 OK
Content-Type: application/json

{
    "type": "OK",
    "message": "ok",
    "data": {
        "memberId": 1,
        "balance": "60,000,000 원"
    }
}
```
## 4. 개발시 고민한 부분
### 4.1.DataBase 설계시 고려 사항
- 각 테이블 간의 관계를 명확히 하고 적절한 제약조건을 통해 데이터 무결성 보장(ex: 회원 테이블의 email 에 unique 선언)  
- 고객, 월렛, 거래 내역, 상품, 구매 내역 간의 명확한 관계 설정
- 향후 기능 확장 및 변경이 용이하도록 확장성을 염두하고 테이블 설계
- 적절한 인덱스를 추가하여 효율적인 데이터 조회
### 4.2.DataBase Table 
* 고객 정보 (member)
  * 고객의 기본 정보와 입출금 계좌 정보 저장
* 월렛 (wallet)
  * 고객 별 잔액 정보 저장
  * optimistic lock을 사용하여 동시성 문제 해결
* 입출금 내역 (wallet_transaction)
  * 모든 거래 내역 저장
  * 거래 유형, 금액, 시간 등을 기록하여 추적 가능
* 상품 정보 (product)
  * 쿠폰 및 일반 상품 정보 저장
  * 상품 유형에 따라 가격 및 기타 정보 저장
* 상품 구매 내역 (purchase)
  * 고객의 상품 구매 내역 저장 
  * 구매 취소 여부와 취소 시간 기록

### 4.3.CODE 구현 시 고민한 부분
* 트랜잭션 처리
  * 서비스별로 tx 전파 속성을 제어하여 예상치 못한 tx 오류 차단
* 중복 출금 방지
  * 일 최대 3,000만원. 일회 최대 1,000만원의 정의된 출금 제약조건이 있음.
  * 중복 출금 방지에 대한 명확한 정의가 어려움. (ex: 1분이내 동일한 출금 요청이 존재하면 후속 요청은 처리하지 않음)
  * 많이 고민해 본후 아무래도 전달하는 의미가 중복 출금을 동시성 처리(concurrency control)에 대한 제어를 요청하는 것으로 보여 해당 관점으로 개발을 진행.
  * 동시성 처리는 낙관적 락(Optimistic Lock) -> 비관적 락(Pessimistic Lock) -> 분산락(Distributed Lock) 순으로 모니터링 하면서 적용하는 것으로 항상 작업을 진행한 경험이 있어서 해당 서비스에서는 우선적으로 낙관적 락을 적용시킴
  * 낙관적 락을 wallet table 에 적용하였을 경우 출금 처리 뿐만 아니라 입금 처리로 동시성 처리가 가능해짐.
* 입출금 처리 
  * 쿠폰을 구매/취소 할 경우 wallet_transaction 에서도 기록을 남기고, wallet 에 잔액도 변경해야됨.
  * 입금(deposit), 출금(withdraw) 이라는 행위로 모두 묶어서 처리 하기 위해 code 를 고민하여 작성.
* 테스트
  * 절차적 테스트 코드가 실행 될 수 있도록 작성하여 통해 요건에 대한 Happy Case 는 모두 통과 하도록 함.
### 4.4.추가 구현한 것들
* springboot 2.X 버전은 2023년 11월부로 지원을 종료(https://spring.io/projects/spring-boot#support)하여 springboot3.3 버전을 기준으로 project 를 구성.
  * 3.3 버전 처리를 하면서 추가적으로 JPA3 적용으로 기존의 javax -> jakarta 로 변경되는 이슈등으로 인해 환경 설정에 추가 시간이 소모됨.
* Spring security 의 AccessDeniedHandler, AuthenticationEntryPoint 를 사용하지 않고, filter 에서 인증 실패 처리 구현. 

### 4.5.구현 못한 것들
* controller, repository, entity 등에 대하여 별도의 unit test case 작성을 못함.
* JWT 에 refresh_token 이 없고, token refresh endpoint 를 구현하지 않음.