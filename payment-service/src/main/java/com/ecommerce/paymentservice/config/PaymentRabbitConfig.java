package com.ecommerce.paymentservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentRabbitConfig {

    // Cola donde este servicio escuchará comandos del Order Service
    public static final String PAYMENT_COMMAND_QUEUE = "payment.command.queue";
    // Cola de respuesta donde el Order Service estará esperando eventos
    public static final String ORDER_PAYMENT_REPLY_QUEUE = "order.payment.reply.queue";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue paymentCommandQueue() {
        return new Queue(PAYMENT_COMMAND_QUEUE, true);
    }

    // NOTA: No es estrictamente necesario declarar la cola de REPLY aquí,
    // pero declararla en el Order Service SÍ es obligatorio.
}