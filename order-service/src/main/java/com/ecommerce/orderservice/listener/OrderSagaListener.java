package com.ecommerce.orderservice.listener;


import com.ecommerce.commonevents.commands.ProcessPaymentCommand;
import com.ecommerce.commonevents.commands.ReleaseInventoryCommand;
import com.ecommerce.commonevents.events.InventoryReservedEvent;
import com.ecommerce.commonevents.events.PaymentCompletedEvent;
import com.ecommerce.commonevents.events.PaymentFailedEvent;
import com.ecommerce.commonevents.events.InventoryRejectedEvent;
import com.ecommerce.orderservice.config.OrderRabbitConfig;
import com.ecommerce.orderservice.model.OrderStatus;
import com.ecommerce.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class OrderSagaListener {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    // Escucha eventos de RECHAZO de inventario
    // USA LA CONSTANTE AQUÍ, NO EL STRING HARDCODEADO
    @RabbitListener(queues = OrderRabbitConfig.ORDER_INVENTORY_REJECTED_QUEUE)
    public void handleInventoryRejected(InventoryRejectedEvent event) {
        System.out.println("Recibido InventoryRejectedEvent para orden: " + event.getOrderId());

        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus(String.valueOf(OrderStatus.REJECTED));
            orderRepository.save(order);
            System.out.println("Orden " + order.getId() + " actualizada a REJECTED. Motivo: " + event.getReason());
        });
    }

    // **********************************************
    // 1. INICIO DEL PAGO (Después de la reserva de inventario)
    // **********************************************
    @RabbitListener(queues = OrderRabbitConfig.ORDER_INVENTORY_REPLY_QUEUE)
    public void handleInventoryReserved(InventoryReservedEvent event) {
        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus(String.valueOf(OrderStatus.PENDING_PAYMENT));
            orderRepository.save(order);
            System.out.println("Order Service: Orden " + order.getId() + " a PENDING_PAYMENT");

            // Generamos un monto para el pago. Usaremos la cantidad y un precio fijo.
            // NOTA: Asumimos que tienes un campo totalAmount o lo calculas.
            BigDecimal amount = new BigDecimal(event.getQuantity() * 100);

            ProcessPaymentCommand command = new ProcessPaymentCommand(
                    event.getOrderId(),
                    amount
            );
            // Enviamos el comando a la cola de pago
            rabbitTemplate.convertAndSend(OrderRabbitConfig.PAYMENT_COMMAND_QUEUE, command);
            System.out.println("Order Service: Comando de pago enviado.");
        });
    }

    // **********************************************
    // 2. FIN DE SAGA: PAGO EXITOSO
    // **********************************************
    @RabbitListener(queues = OrderRabbitConfig.ORDER_PAYMENT_REPLY_QUEUE)
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus(String.valueOf(OrderStatus.COMPLETED)); // [cite: 238]
            orderRepository.save(order);
            System.out.println("Order Service: SAGA FINALIZADA con ÉXITO. Orden " + order.getId() + " completada.");
            // Opcional: Aquí enviarías OrderCompletedEvent [cite: 239]
        });
    }

    // **********************************************
    // 3. FALLO DE PAGO: INICIO DE COMPENSACIÓN
    // **********************************************
    @RabbitListener(queues = OrderRabbitConfig.ORDER_PAYMENT_REPLY_QUEUE)
    public void handlePaymentFailed(PaymentFailedEvent event) {
        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            // Paso 1: Cambiar estado a CANCELLED [cite: 241]
            order.setStatus(String.valueOf(OrderStatus.CANCELLED));
            orderRepository.save(order);
            System.err.println("Order Service: Pago fallido. Orden " + order.getId() + " CANCELADA.");

            // Paso 2: Enviar ReleaseInventory Command para compensar la reserva [cite: 242]
            ReleaseInventoryCommand compensationCommand = new ReleaseInventoryCommand(
                    event.getOrderId(),
                    order.getProductId(),
                    order.getQuantity() // Usar la cantidad original
            );
            // Enviamos a la cola de compensación de inventario
            rabbitTemplate.convertAndSend(OrderRabbitConfig.INVENTORY_COMPENSATION_QUEUE, compensationCommand);
            System.err.println("Order Service: Comando de compensación (ReleaseInventoryCommand) enviado.");
        });
    }

}