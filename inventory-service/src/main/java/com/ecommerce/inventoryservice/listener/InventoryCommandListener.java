package com.ecommerce.inventoryservice.listener;

import com.ecommerce.commonevents.commands.ReserveInventoryCommand;
import com.ecommerce.commonevents.events.InventoryRejectedEvent;
import com.ecommerce.commonevents.events.InventoryReservedEvent;
import com.ecommerce.inventoryservice.model.InventoryItem;
import com.ecommerce.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InventoryCommandListener {

    private final InventoryRepository inventoryRepository;
    private final RabbitTemplate rabbitTemplate;

    // Asegúrate de que este nombre de cola coincida con el definido en Order Service
    @RabbitListener(queues = "inventory.command.queue")
    @Transactional
    public void handleReserveInventory(ReserveInventoryCommand command) {
        System.out.println("Recibido comando de reserva para producto: " + command.getProductId());

        var inventoryItemOpt = inventoryRepository.findByProductId(command.getProductId());

        if (inventoryItemOpt.isPresent() && inventoryItemOpt.get().getAvailableQuantity() >= command.getQuantity()) {
            // --- CASO ÉXITO ---
            InventoryItem item = inventoryItemOpt.get();
            item.setAvailableQuantity(item.getAvailableQuantity() - command.getQuantity());
            inventoryRepository.save(item);

            InventoryReservedEvent event = new InventoryReservedEvent(
                    command.getOrderId(),
                    command.getProductId(),
                    command.getQuantity()
            );
            // Enviar a la cola de respuestas de Order Service
            rabbitTemplate.convertAndSend("order.inventory.reply.queue", event);
            System.out.println("Stock reservado. Evento enviado.");

        } else {
            // --- CASO FALLO ---
            InventoryRejectedEvent event = new InventoryRejectedEvent(
                    command.getOrderId(),
                    command.getProductId(),
                    command.getQuantity(),
                    "Stock insuficiente o producto no encontrado"
            );
            // Puedes enviar a la misma cola de respuesta si Order Service sabe manejar ambos tipos,
            // o a una cola específica de rechazos si así lo configuraste.
            // Por simplicidad, usaremos la misma reply queue si tu OrderSagaListener puede distinguirlos,
            // o la cola de rechazos si la creaste. Asumamos la cola de rechazos por claridad:
            rabbitTemplate.convertAndSend("order.inventory.rejected.queue", event);
            System.out.println("Stock insuficiente. Evento de rechazo enviado.");
        }
    }
}