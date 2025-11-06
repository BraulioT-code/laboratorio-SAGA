package com.ecommerce.inventoryservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory")
@Data
@AllArgsConstructor // <- ESTO ES CLAVE para que funcione el new InventoryItem(...)
@NoArgsConstructor
public class InventoryItem {
    @Id
    private String id;
    private String productId;
    private int availableQuantity;
}