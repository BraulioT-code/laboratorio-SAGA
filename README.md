# 🧩 Laboratorio SAGA - E-commerce

Proyecto desarrollado en **Spring Boot** con arquitectura de **microservicios** y el **patrón SAGA** para la gestión distribuida de transacciones.
Cada microservicio se comunica de manera asíncrona mediante **RabbitMQ**.

---

## 🚀 Progreso del Laboratorio

| Parte | Descripción | Estado |
|:------|:-------------|:--------|
| 1️⃣ | Estructura inicial con Spring Boot y base H2 | ✅ Completado |
| 2️⃣ | Integración de RabbitMQ real con Docker | ✅ Completado |
| 3️⃣ | **Implementación del flujo Saga: Inventario (Avance y Rechazo)** | ✅ Completado |
| 4️⃣ | **Implementación de Compensación (Pago y Rollback)** | ⏳ En progreso |
| 5️⃣ | Pruebas integradas y despliegue | ⏳ Pendiente |

---

## 🧱 Estructura del Proyecto

laboratorio-SAGA/

├── common-events/ # Librería compartida (eventos y comandos)

├── order-service/ # Servicio de pedidos

├── inventory-service/ # Servicio de inventario

├── payment-service/ # Servicio de pagos

├── docker-compose.yml # Broker RabbitMQ en contenedor

└── README.md # Este archivo


---

## ⚙️ Tecnologías Utilizadas

- **Java 17+**
- **Spring Boot 3.5.7**
- **Spring Data JPA (H2)**
- **RabbitMQ (mensajería asíncrona)**
- **Maven**
- **Docker / Docker Compose**

---

## 🐇 Configuración de RabbitMQ

Para levantar RabbitMQ en Docker:

```bash
docker compose up -d

Interfaz de administración disponible en: 👉 http://localhost:15672

Usuario: admin

Contraseña: admin
```
Ejemplo: Order Service
cd order-service ./mvnw spring-boot:run

El puerto de cada uno:

order-service → 8081

inventory-service → 8082

payment-service → 8083

Laboratorio académico: Patrón Saga en microservicios con Spring Boot y RabbitMQ

Desarrollado por: Braulio Tovar