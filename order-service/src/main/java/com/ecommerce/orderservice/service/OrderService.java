package com.ecommerce.orderservice.service;

import com.ecommerce.commonevents.commands.ReserveInventoryCommand;
import com.ecommerce.orderservice.config.OrderRabbitConfig;
import com.ecommerce.orderservice.dto.CreateOrderRequest;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderStatus;
import com.ecommerce.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        // 1. Crear y guardar la orden con estado inicial
        Order order = new Order();
        order.setId(String.valueOf(UUID.randomUUID()));
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setStatus(String.valueOf(OrderStatus.CREATED));

        Order savedOrder = orderRepository.save(order);

        // 2. Iniciar la Saga: Enviar comando a Inventory Service
        ReserveInventoryCommand command = new ReserveInventoryCommand(
                savedOrder.getId(),
                savedOrder.getProductId(),
                savedOrder.getQuantity()
        );

        // Enviamos el mensaje a la cola de comandos de inventario
        rabbitTemplate.convertAndSend(OrderRabbitConfig.INVENTORY_COMMAND_QUEUE, command);

        System.out.println("Orden creada: " + savedOrder.getId() + ". Comando de reserva enviado.");
        return savedOrder;
    }
}