package com.ecommerce.paymentservice.listener;

import com.ecommerce.commonevents.commands.ProcessPaymentCommand;
import com.ecommerce.commonevents.events.PaymentCompletedEvent;
import com.ecommerce.commonevents.events.PaymentFailedEvent;
import com.ecommerce.paymentservice.config.PaymentRabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PaymentCommandListener {

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = PaymentRabbitConfig.PAYMENT_COMMAND_QUEUE)
    public void handleProcessPayment(ProcessPaymentCommand command) {
        System.out.println("Payment Service: Recibido comando de pago para orden: " + command.getOrderId());

        // Simulación: Falla si el monto es mayor a 1000.00
        boolean paymentSuccessful = simulatePayment(command.getAmount());

        if (paymentSuccessful) {
            // Caso Éxito
            PaymentCompletedEvent event = new PaymentCompletedEvent(command.getOrderId());
            rabbitTemplate.convertAndSend(PaymentRabbitConfig.ORDER_PAYMENT_REPLY_QUEUE, event);
            System.out.println("Payment Service: Pago exitoso. Evento PaymentCompletedEvent enviado.");
        } else {
            // Caso Fallo (Activa Compensación)
            PaymentFailedEvent event = new PaymentFailedEvent(command.getOrderId(), "Monto excede el límite de 1000.00.");
            rabbitTemplate.convertAndSend(PaymentRabbitConfig.ORDER_PAYMENT_REPLY_QUEUE, event);
            System.err.println("Payment Service: Pago fallido. Evento PaymentFailedEvent enviado. ACTIVANDO COMPENSACIÓN.");
        }
    }

    private boolean simulatePayment(BigDecimal amount) {
        // La simulación falla si el monto es mayor a 1000.00
        return amount.compareTo(new BigDecimal("1000.00")) <= 0;
    }
}