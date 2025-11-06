package com.ecommerce.commonevents.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class OrderCancelledEvent {
    private String orderId;
    private String reason;
}