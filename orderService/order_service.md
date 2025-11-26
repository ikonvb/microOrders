1. **Order Service**

Технологии:

1. Spring Boot,
2. Spring Web
3. gRPC -клиент
4. Spring Kafka
5. Spring Security
6. Spring Data JPA + PostgreSQL

Функционал:

1. Rest API контроллеры:

auth-controller:
/auth/reg,
/auth/login,
/auth/refresh,

order-controller:
POST /api/order – создание заказа,

CRUD-controller для таблицы user

1. gRPC клиент – запрос в Inventory Service для проверки наличия товара
2. Kafka Producer