package com.ecommerce.orderservice.messaging.commands;

import java.io.Serializable;

public class ReserveInventoryCommand implements Serializable {
    private String orderId;
    private String productId;
    private int quantity;

    public ReserveInventoryCommand() {}
    public ReserveInventoryCommand(String orderId, String productId, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getOrderId() { return orderId; }
    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
}