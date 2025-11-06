package com.ecommerce.orderservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import jakarta.persistence.Table; // ¡Importante agregar este import!

@Entity
@Table(name = "shop_orders") // <- AGREGA ESTA LÍNEA
public class Order {

    @Id
    private String id;
    private String productId;
    private int quantity;
    private BigDecimal totalAmount;
    private String status;

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
