package com.ecommerce.inventoryservice.messaging;

import com.ecommerce.commonevents.commands.ReserveInventoryCommand;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryListener {

    @RabbitListener(queues = "inventory.queue")
    public void handleReserveInventory(ReserveInventoryCommand command) {
        System.out.println("📦 Recibido comando de reserva de inventario: "
                + command.getProductId() + " x" + command.getQuantity());

    }
}
