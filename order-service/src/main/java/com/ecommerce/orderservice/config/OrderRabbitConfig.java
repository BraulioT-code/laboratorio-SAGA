package com.ecommerce.orderservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderRabbitConfig {
    // Cola donde Inventory escuchará comandos
    public static final String INVENTORY_COMMAND_QUEUE = "inventory.command.queue";
    // Cola donde Order escuchará eventos de respuesta de inventario
    public static final String ORDER_INVENTORY_REPLY_QUEUE = "order.inventory.reply.queue";
    public static final String ORDER_INVENTORY_REJECTED_QUEUE = "order.inventory.rejected.queue";

    // CONSTANTES NUEVAS PARA PAGO
    public static final String PAYMENT_COMMAND_QUEUE = "payment.command.queue";
    public static final String ORDER_PAYMENT_REPLY_QUEUE = "order.payment.reply.queue";

    public static final String INVENTORY_COMPENSATION_QUEUE = "inventory.compensation.queue";

    @Bean
    public Queue inventoryCommandQueue() {
        return new Queue(INVENTORY_COMMAND_QUEUE, true);
    }

    @Bean
    public Queue orderInventoryReplyQueue() {
        return new Queue(ORDER_INVENTORY_REPLY_QUEUE, true);
    }

    @Bean
    public Queue orderInventoryRejectedQueue() {
        return new Queue(ORDER_INVENTORY_REJECTED_QUEUE, true);
    }

    // BEAN NUEVO: Cola que escucha los comandos de pago (la define el consumidor Payment Service)
    @Bean
    public Queue paymentCommandQueue() {
        return new Queue(PAYMENT_COMMAND_QUEUE, true);
    }

    // BEAN NUEVO: Cola donde el Order Service escuchará las respuestas de Payment Service
    @Bean
    public Queue orderPaymentReplyQueue() {
        return new Queue(ORDER_PAYMENT_REPLY_QUEUE, true);
    }

    // Bean para declarar la cola de compensación (la que el inventory-service escuchará)
    @Bean
    public Queue inventoryCompensationQueue() {
        return new Queue(INVENTORY_COMPENSATION_QUEUE, true);
    }
}