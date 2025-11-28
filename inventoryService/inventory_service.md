4. **Inventory Service**

Технологии:

1. Spring Boot,
2. gRPC -сервер
3. Spring Data JPA + PostgreSQL

Функционал:

1. gRPC сервер (InventoryServiceGrpc) – ендпоинт checkAvailability(ProductRequest) – возвращает ProductResponse (вся информация о товаре, в том числе его количество)
2. БД: таблица products (id, name, quantity, price, sale)
3. CRUD-контроллер для таблицы products:

GET /api/products

GET /api/products/{product_id}

POST /api/products

DELETE /api/products/{product_id}