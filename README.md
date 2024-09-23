

> # 위치 기반 맛집 제공 서비스를 제공
인스타그램, 스레드, 페이스북, 트위터 등 여러 SNS에 게시된 특정 해시태그를 기반으로, 관련 게시물을 한 곳에서 통합적으로 확인할 수 있는 웹서비스


## 개요
사용자는 통합된 게시물들을 상세 조회하고 공유하고 좋아요을 누를 수 있으며, 원하는 해시태그를 검색하여 게시물 목록을 조회할 수 있습니다. 
이를 통해 본 서비스의 고객은 하나의 채널로 유저, 또는 브랜드 의 SNS 노출 게시물 및 통계를 확인할 수 있습니다.

## 개발 기간
2024.08.20 - 2024.08.26

## 팀 구성 및 역할
|이름|역할|
|------|---|
|000|회원 가입|
|000|게시물 공유|
|000|게시물 목록 조회|
|000|게시물 상세 조회|
|최미선|JWT Token 인증|
|000|통계|

## ERD
![image](https://github.com/user-attachments/assets/83479aee-5754-468e-932b-877c60074641)


## 개발 환경
#### [언어 및 프레임워크] 
<div>
  <img alt="SpringBoot" src ="https://img.shields.io/badge/Spring Boot-6DB33F.svg?&style=for-the-badge&logo=Spring Boot&logoColor=white"/> 
<!--   <img alt="SpringSecurity" src ="https://img.shields.io/badge/Spring Security-6DB33F.svg?&style=for-the-badge&logo=springsecurity&logoColor=white"/> 
  <img alt="Hibernate" src ="https://img.shields.io/badge/hibernate-59666C.svg?&style=for-the-badge&logo=hibernate&logoColor=white"/> 
  <img alt="JPA" src ="https://img.shields.io/badge/JPA-6DB33F.svg?&style=for-the-badge&logo=jpa&logoColor=white"/>  -->
  <img alt ="JAVA" src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">   
</div>


#### [데이터베이스] 
<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white">

#### [협업 도구]
<div>
    <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
    <img src="https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white">
</div>


#### [기타]
<img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white"> 


## 주요 기능
- csv 파일을 이용한 db 구축 기능
- Refresh Token은 Redis에 저장
- Access Token이 만료될 경우 프론트에 401반환
- 프론트에서 Refresh Token을 이용한 Access Token 재발급 요청
- 서버에서 인증 후 재발급 혹은 재로그인 요구

## 이슈 트래킹
![image](https://github.com/user-attachments/assets/24504b2c-4a4d-455e-b354-b10649ec910d)


