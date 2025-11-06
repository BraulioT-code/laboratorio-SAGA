package com.ecommerce.commonevents.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class InventoryReservedEvent {
    private String orderId;
    private String productId;
    private int quantity;
}