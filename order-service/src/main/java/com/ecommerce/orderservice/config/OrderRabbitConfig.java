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
}