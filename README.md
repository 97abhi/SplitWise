# SplitWise
A SplitWise App

Functional Requirements
1. User Management
2. Group Management
3. Expense Management
4. Debt Calculation and simplification
5. Notoifications
6. Authentication and Authorization
7. Activity History
8. Activity Analytics


Microservices Overview

    Microservice    ----------- Responibility

1.  User Service                Register, Manage user profile
2.  Group Service               Create/manage groups and members
3.  Expense Service             Add, edit , delete expense
4.  Split Service               Calculate and simplify debt
5. Payment Service              Track Payment status between users
6. Notification Service	        Send email/notification
7. Auth Service	                Token-based authentication (JWT/OAuth2)
8. Activity Service	            Log all actions for history view
9. API Gateway	                Entry point for all services
10. Service Registry	        For service discovery (e.g., Eureka)
11. Config Server	            Centralized config management

Microservice interactions

1. Add Expense
    API Gateway --> Expense service
    Expense Service calls
        1. User service (to get user info)
        2. Group service (to verify group)
        3. Split service (to calculate who owes whom)
        4. Activity Service(to log action)

    Simplify debts
        1. API Gateway -->Split service 
        2. Algorithm like Splitwise debt simplification

2. View Balances
    1. API gateway --> Payment servcies --> Split service

3. Make Payment
    1. API gateway --> Payment Services 
    2. Payment Services --> Update Balances +  call Activity Service


DataBase Design

1. user     
    User (user_id, name, email, phone, created_at)
2. Group Service DB
    Group (group_id, name, created_by, created_at)
    Group_Member (group_id, user_id, joined_at)
3. Expense Service DB
    Expense (expense_id, group_id, paid_by, amount, description, created_at)
    Expense_Share (expense_id, user_id, amount_owed)

4. Payment Service DB
    Payment (payment_id, payer_id, payee_id, amount, status, timestamp)

5. Split Services DB
    User_Balance (user_id, owes_to_user_id, amount)


Authentication (Auth Service)

1. JWT for access tokens
2. Refresh token mechanism 
3. Oauth2 (Google/facebook)


Notification Service

1. Email or push using :
    SMTP
    Firebase(push)
    SNS (for scaling with AWS) --> research

Communication 
1. sync : REST API's via API Gateway
2. Async : Kafka  
    for 
        1. Activity Logs
        2. Notifications
        3. Debt recalculations


splitwise-microservices/
├── api-gateway/
├── auth-service/
├── user-service/
├── group-service/
├── expense-service/
├── split-service/
├── payment-service/
├── notification-service/
|__ audit_service/
├── activity-service/
├── config-server/
├── service-registry/


