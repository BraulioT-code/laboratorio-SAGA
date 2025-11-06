package com.ecommerce.orderservice.dto;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private String productId;
    private int quantity;

    // Getters y setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}