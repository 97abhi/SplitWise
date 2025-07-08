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

