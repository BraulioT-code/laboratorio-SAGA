package com.ecommerce.orderservice.model;

public enum OrderStatus {
    CREATED,
    PENDING_PAYMENT,
    COMPLETED,
    CANCELLED,
    REJECTED
}