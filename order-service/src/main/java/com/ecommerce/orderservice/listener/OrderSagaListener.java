package com.ecommerce.orderservice.listener;

import com.ecommerce.commonevents.events.InventoryRejectedEvent;
import com.ecommerce.commonevents.events.InventoryReservedEvent;
import com.ecommerce.orderservice.config.OrderRabbitConfig;
import com.ecommerce.orderservice.model.OrderStatus;
import com.ecommerce.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderSagaListener {

    private final OrderRepository orderRepository;

    // Escucha eventos de ÉXITO de inventario
    @RabbitListener(queues = OrderRabbitConfig.ORDER_INVENTORY_REPLY_QUEUE)
    public void handleInventoryReserved(InventoryReservedEvent event) {
        System.out.println("Recibido InventoryReservedEvent para orden: " + event.getOrderId());

        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            // Asegúrate de que tu entidad Order tenga el campo status como String si usas String.valueOf
            order.setStatus(String.valueOf(OrderStatus.PENDING_PAYMENT));
            orderRepository.save(order);
            System.out.println("Orden " + order.getId() + " actualizada a PENDING_PAYMENT");
        });
    }

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
}