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
| 4️⃣ | **Implementación de Compensación (Pago y Rollback de Inventario)** | ✅ Completado |
| 5️⃣ | Pruebas integradas (postman)| ✅ Completado |

---

## 🧱 Estructura del Proyecto

laboratorio-SAGA/

├── common-events/ # Librería compartida (eventos y comandos)

├── order-service/ # Servicio de pedidos (Orquestador de la Saga) 

├── inventory-service/ # Servicio de inventario (Gestiona el inventario) 

├── payment-service/ # Servicio de pagos (Gestiona el procesamiento de pagos simulado) 

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
````

Interfaz de administración disponible en:
👉 http://localhost:15672

Usuario: admin

Contraseña: admin

# Ejemplo: Microservicios

El puerto y el comando de ejecución de cada microservicio son:

| Microservicio | Puerto | Comando de Ejecución (desde la raíz) |
|:--------------|:-------|:------------------------------------|
| order-service | 8081 | `.\mvnw -pl order-service spring-boot:run` |
| inventory-service | 8082 | `.\mvnw -pl inventory-service spring-boot:run` |
| payment-service | 8083 | `.\mvnw -pl payment-service spring-boot:run` |

## 🎯 Flujos de la Saga Implementados

El sistema implementa el Patrón Saga por Orquestación para cubrir tres escenarios principales:

1.  **Caso Exitoso (Transacción Completa):** Reserva OK $\rightarrow$ Pago OK $\rightarrow$ Orden `COMPLETED`.
2.  **Fallo en Inventario:** Sin stock $\rightarrow$ Orden `REJECTED`.
3.  **Fallo en Pago (Compensación):** Reserva OK $\rightarrow$ Pago Falla $\rightarrow$ Orden `CANCELLED` y se envía `ReleaseInventoryCommand`.

-----

Laboratorio académico: Patrón Saga en MicroServicios con Spring Boot y RabbitMQ

Desarrollado por: Braulio Tovar, Jonathan Vega

-----
