
<img src="https://raw.githubusercontent.com/nikhilz/ng8-myEMS/master/src/assets/images/logo-small.jpg" width="100" height="100">

# springboot2-security-jwt

Frontend : https://github.com/nikhilz/ng8-JwtAuth-Frontend

 Spring Boot Token based Authentication with Spring Security & JWT
 
  `Concepts Covered :`
 
- Appropriate Flow for User Signup & User Login with JWT Authentication
- Spring Boot Application Architecture with Spring Security
- How to configure Spring Security to work with JWT
- How to define Data Models and association for Authentication and Authorization
- Way to use Spring Data JPA to interact with H2 Database

# Spring RestAPIs Controllers

`Controller for Authentication`

 This controller provides APIs for register and login actions
 > /api/auth/signup
- check existing username/email
- create new User (with ROLE_USER if not specifying role)
- save User to database using UserRepository

 > /api/auth/signin
- authenticate { username, pasword }
- update SecurityContext using Authentication object
- generate JWT
- get UserDetails from Authentication object
- response contains JWT and UserDetails data

# Controller for testing Authorization

There are 4 APIs:
- /api/test/all for public access
- /api/test/user for users has ROLE_USER or ROLE_MODERATOR or ROLE_ADMIN
- /api/test/mod for users has ROLE_MODERATOR
- /api/test/admin for users has ROLE_ADMIN
