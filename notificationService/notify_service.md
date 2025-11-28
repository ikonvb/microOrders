1. **Notification Service**

Технологии:

1. Spring Boot,
2. Spring Kafka
3. Spring Data JPA + PostgreSQL

Функционал:

1. Kafka Consumer – слушает топик orders и сохраняет в таблицу orders
2. Таблица orders (id, order_id, product_id, quantity, price, sale, total_price, user_id)
3. Read-controller для вывода данных из таблицы orders. Ендпоинты:

GET /api/orders/all – вывод всей таблицы заказов

GET /api/orders/{order_id} – вывод всех покупок по одному заказу

GET /api/orders/{user_id} – вывод всех покупок по одному юзеру