package com.ecommerce.commonevents.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class PaymentCompletedEvent {
    private String orderId;
    // Puedes agregar más datos si los necesitas, ej: transactionId
}