package com.ecommerce.inventoryservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryRabbitConfig {

    // 1. Cola que consume los comandos de avance de la Saga (Reserva)
    public static final String INVENTORY_COMMAND_QUEUE = "inventory.command.queue";

    // 2. Cola que consume los comandos de Rollback/Compensación
    public static final String INVENTORY_COMPENSATION_QUEUE = "inventory.compensation.queue";

    // 3. Cola a la que envía eventos de ÉXITO de reserva (Order Service escucha)
    public static final String ORDER_INVENTORY_REPLY_QUEUE = "order.inventory.reply.queue";

    // 4. Cola a la que envía eventos de RECHAZO de reserva (Order Service escucha)
    public static final String ORDER_INVENTORY_REJECTED_QUEUE = "order.inventory.rejected.queue";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Beans de cola que este servicio CONSUME
    @Bean
    public Queue inventoryCommandQueue() {
        return new Queue(INVENTORY_COMMAND_QUEUE, true);
    }

    @Bean
    public Queue inventoryCompensationQueue() {
        return new Queue(INVENTORY_COMPENSATION_QUEUE, true);
    }

    // Beans de cola a la que este servicio PRODUCE (aunque Order Service las define,
    // es buena práctica declararlas para asegurar la consistencia)
    @Bean
    public Queue orderInventoryReplyQueue() {
        return new Queue(ORDER_INVENTORY_REPLY_QUEUE, true);
    }

    @Bean
    public Queue orderInventoryRejectedQueue() {
        return new Queue(ORDER_INVENTORY_REJECTED_QUEUE, true);
    }
}