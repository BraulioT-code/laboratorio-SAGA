package com.ecommerce.inventoryservice.listener;

import com.ecommerce.commonevents.commands.ReserveInventoryCommand;
import com.ecommerce.commonevents.commands.ReleaseInventoryCommand; // Importar el comando de compensación
import com.ecommerce.commonevents.events.InventoryRejectedEvent;
import com.ecommerce.commonevents.events.InventoryReservedEvent;
import com.ecommerce.inventoryservice.config.InventoryRabbitConfig; // Asumimos esta clase de configuración
import com.ecommerce.inventoryservice.model.InventoryItem;
import com.ecommerce.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InventoryCommandListener {

    private final InventoryRepository inventoryRepository;
    private final RabbitTemplate rabbitTemplate;

    // **********************************************
    // LÓGICA DE RESERVA DE INVENTARIO (Flujo de Avance - Parte 3)
    // **********************************************

    // Escucha la cola de comandos de reserva
    @RabbitListener(queues = InventoryRabbitConfig.INVENTORY_COMMAND_QUEUE)
    @Transactional
    public void handleReserveInventory(ReserveInventoryCommand command) {
        System.out.println("Inventory Service: Recibido comando de reserva para producto: " + command.getProductId());

        Optional<InventoryItem> inventoryItemOpt = inventoryRepository.findByProductId(command.getProductId());

        if (inventoryItemOpt.isPresent() && inventoryItemOpt.get().getAvailableQuantity() >= command.getQuantity()) {
            // --- CASO ÉXITO: Reservar Stock ---
            InventoryItem item = inventoryItemOpt.get();
            item.setAvailableQuantity(item.getAvailableQuantity() - command.getQuantity());
            inventoryRepository.save(item);

            InventoryReservedEvent event = new InventoryReservedEvent(
                    command.getOrderId(),
                    command.getProductId(),
                    command.getQuantity()
            );
            // Enviar a la cola de respuestas de Order Service
            rabbitTemplate.convertAndSend(InventoryRabbitConfig.ORDER_INVENTORY_REPLY_QUEUE, event);
            System.out.println("Inventory Service: Stock reservado. Evento InventoryReservedEvent enviado.");

        } else {
            // --- CASO FALLO: Rechazo por falta de Stock ---
            InventoryRejectedEvent event = new InventoryRejectedEvent(
                    command.getOrderId(),
                    command.getProductId(),
                    command.getQuantity(),
                    "Stock insuficiente o producto no encontrado."
            );
            // Enviar a la cola de rechazos
            rabbitTemplate.convertAndSend(InventoryRabbitConfig.ORDER_INVENTORY_REJECTED_QUEUE, event);
            System.err.println("Inventory Service: Stock insuficiente. Evento InventoryRejectedEvent enviado.");
        }
    }

    // **********************************************
    // LÓGICA DE COMPENSACIÓN (Flujo de Rollback - Parte 4)
    // **********************************************

    // Escucha la cola de compensación (ReleaseInventoryCommand, enviado por Order Service tras fallo de pago)
    @RabbitListener(queues = InventoryRabbitConfig.INVENTORY_COMPENSATION_QUEUE) // La cola de compensación
    @Transactional
    public void handleReleaseInventory(ReleaseInventoryCommand command) {
        System.err.println("Inventory Service: Recibido comando de COMPENSACIÓN para orden: " + command.getOrderId());

        Optional<InventoryItem> inventoryItemOpt = inventoryRepository.findByProductId(command.getProductId());

        if (inventoryItemOpt.isPresent()) {
            InventoryItem item = inventoryItemOpt.get();
            // Acción compensatoria: Sumar la cantidad reservada de vuelta al stock disponible
            item.setAvailableQuantity(item.getAvailableQuantity() + command.getQuantity());
            inventoryRepository.save(item);

            // Opcional: Emitir InventoryReleasedEvent para logging o notificación.

            System.out.println("Inventory Service: Compensación aplicada. Stock liberado (" + command.getQuantity() + ") para producto: " + command.getProductId());
        } else {
            System.err.println("Inventory Service: Error de compensación. Producto no encontrado para liberar stock.");
        }
    }
}