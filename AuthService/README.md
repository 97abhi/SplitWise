# SplitWise
Auth Service

Following questions were asked during creation of entity classes
1. @PrePersist
    What it is: A JPA lifecycle callback.
    When it runs: Just before inserting a new record into the database.
    Purpose: we can use it to set fields like created_at or issued_at automatically.

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

2. @PreUpdate
    What it is: Another JPA lifecycle callback.
    When it runs: Just before updating an existing record.
    Purpose: we can use it to update updated_at timestamps automatically.

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
3. Difference Between long and Long
    Feature	long (primitive)	      Long (Wrapper class)
    Feature	long (primitive)	      Long (Wrapper class)
    Can be null	    ❌ No	        ✅ Yes
    Default value	    0	            null
    Use in JPA	    ❌ Not preferred	✅ Preferred (null-safe)

 Use Long in entity classes because database values can be null.

4. Why token has length = 512
    Reason: JWTs or OAuth tokens are usually long strings.
    Lengths can go above 200–300 characters depending on payload.
    512 ensures we are safe and don’t get DataTooLongException.

    Example JWT:

    eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9... (total 300+ characters)

5. What is onCreate() and onUpdate()?
    They are custom methods we define and annotate with @PrePersist / @PreUpdate.
    These methods run automatically before insert/update, and we use them to:

    Set timestamps

    Initialize values

    Perform auto-filling logic

6. @Builder Annotation
From Lombok.

Allows we to use the Builder Pattern to create objects.

Useful when wer object has many fields and we want readable, flexible construction.

Without Builder:

AuthToken token = new AuthToken(1L, 2L, "abc", "Bearer", ...);

With Builder:

AuthToken token = AuthToken.builder()
    .userId(1L)
    .token("abc")
    .tokenType("Bearer")
    .build();

7. If table has AUTO_INCREMENT, do we still need @GeneratedValue?
 Yes.

In the DB: AUTO_INCREMENT tells MySQL to generate the ID.

In JPA: @GeneratedValue(strategy = GenerationType.IDENTITY) tells Hibernate to let the database handle the ID generation.

Without this, Hibernate might try to assign the ID manually or throw errors.



Questions while creating repository
1. Why did we choose List<AuthToken> in AuthTokenRepository?

    List<AuthToken> findByUserId(Long userId);
🔹 Reason:
    A user can have multiple tokens — for example:

    Multiple devices logged in at once

    Multiple sessions (e.g., web and mobile apps)

    Different token types (access, refresh)

    So the relationship between user and tokens is One-to-Many, hence List<AuthToken>.

    🔹 Example:
    If user ID 101 has logged in from a laptop and a phone:

  
    [
      { token: "...laptop token...", userId: 101 },
      { token: "...phone token...", userId: 101 }
    ]
    If we instead used:

 
    Optional<AuthToken> findByUserId(Long userId);
    …it would fail or return only one result, which is not desired in this case.

2. Why is it not findRoleByUserId() in UserRoleRepository?


    findRoleByUserId(Long userId)
    Instead, we wrote:

    List<UserRole> findByUserId(Long userId);
    🔹 Reason:
    The UserRole entity represents a mapping (join table) between users and roles.

    We query that mapping table to get all mappings for a given userId.

    If we wanted to directly get Role objects, that would require a custom query or a JOIN.

    🔹 Example use:
  
    List<UserRole> roles = userRoleRepository.findByUserId(101L);
    Then we could extract roleIds from that list or fetch full Role entities from RoleRepository.

    If we Want findRolesByUserId() Directly:
    we could write a custom query like:

 
    @Query("SELECT r FROM Role r JOIN UserRole ur ON r.id = ur.roleId WHERE ur.userId = :userId")
    List<Role> findRolesByUserId(@Param("userId") Long userId);
    …but keeping it in UserRoleRepository and doing the second fetch separately is simpler and modular.

1. What are the dependency names used for securing auth_service?
You typically use the following dependencies in pom.xml:

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
These provide:

Spring Security infrastructure

JWT generation/parsing/validation

2. Difference between @RequestParam and @RequestBody
Feature	            @RequestParam	                                @RequestBody
Purpose	            Extracts query/form parameters	              Extracts JSON/XML body payload
Usage	            @RequestParam("id") Long id	                  @RequestBody UserDTO user
Format	            URL encoded: /login?userId=1	              JSON: { "userId": 1, "password": "xyz" }

3. Difference between ResponseEntity and @ResponseBody
@ResponseBody: Converts return object to JSON/XML and writes it to HTTP response.

ResponseEntity: A wrapper that includes body, HTTP status, and headers (full control).

Example:

@ResponseBody
public String hello() {
    return "Hello";
}

public ResponseEntity<String> greet() {
    return ResponseEntity.status(200).body("Hi");
}

4. Why are we checking for "Bearer " in the JWT auth filter?
JWTs are usually sent in the Authorization header like:

Authorization: Bearer <token>
So we check if the header starts with "Bearer " to:

Validate the format

Extract the token part using substring(7)

5. What is UsernamePasswordAuthenticationToken?
It's a class used by Spring Security to hold user credentials during authentication.

Usage:

UsernamePasswordAuthenticationToken authToken =
    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
It wraps:

Principal (username/userDetails)

Credentials (usually null after login)

Authorities (roles/permissions)

6. What is FilterChain and its doFilter() method?
FilterChain is part of the servlet API.

It allows chaining multiple filters (like JwtAuthenticationFilter, AuthorizationFilter, etc.)


filterChain.doFilter(request, response);
Means: Pass the request to the next filter in the chain.

7. What is SecurityContextHolder?
A Spring Security class that holds security info for the current request/thread.

We set:


SecurityContextHolder.getContext().setAuthentication(authentication);
So Spring knows the user is authenticated and can apply authorization rules later.

8. Why do we need a signing key and what if we don’t use it?
The signing key is used to digitally sign and later verify the JWT.

If you skip signing or use a weak key:

JWT can be forged.

You get errors like: WeakKeyException.

9. What is SignatureAlgorithm.HS256?
It specifies the signing algorithm:

HMAC-SHA256: symmetric key, fast and secure if key is strong.

Must use a key of at least 256 bits (32 bytes) for HS256.

10. What is the Claims class?
Represents the payload section of a JWT (key-value pairs).

Contains:

Standard claims like sub, exp, iat

Custom claims like userId, roles

Example:


Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
String userId = claims.getSubject();


11. How does the Jwts object look like?
JWT has 3 parts:


<Header>.<Payload>.<Signature>
Example:

Header: {
  "alg": "HS256",
  "typ": "JWT"
}
Payload (Claims): {
  "sub": "1234567890",
  "iat": 1516239022
}
Signature: base64UrlEncode(HMAC-SHA256(base64Url(header) + "." + base64Url(payload), key))

12. What is HttpSecurity and how is it used in filterChain?
HttpSecurity allows configuring security rules.

In filterChain() method you define:

http
  .csrf().disable()
  .authorizeHttpRequests()
      .requestMatchers("/auth/**").permitAll()
      .anyRequest().authenticated()
  .and()
  .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
Add filters, set session policy, match request patterns, etc.

13. What is AuthenticationManager and AuthenticationConfiguration?
AuthenticationManager: Handles authentication logic (delegates to UserDetailsService).

AuthenticationConfiguration: A Spring-provided helper to get the AuthenticationManager.


@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
}

in auth_service we dont have useredtailsservice, so do we need authentcation manager?
this touches a very important point in how Spring Security works.

✅ Short Answer:
Yes, you still need AuthenticationManager if you are using Spring Security to authenticate credentials (e.g., userId + password) even if you don’t use UserDetailsService.

But if you are not using Spring Security to do the login, and are manually validating credentials (like checking userId and password from DB yourself), then you don’t strictly need AuthenticationManager.

current setup:

boolean validate = authUserService.validateCredentials(userId, password);
if (!validate) return 401;
String token = jwtService.generateToken(userId);
This is manual credential validation.

You are not calling AuthenticationManager.authenticate() or relying on UserDetailsService.

So you do not need AuthenticationManager or UserDetailsService for login.

✅ When is AuthenticationManager required?
When using Spring Security's login process.

You want to delegate authentication logic to Spring Security like:

Authentication authentication = authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(username, password)
);
This requires:

A UserDetailsService

A configured AuthenticationManager

