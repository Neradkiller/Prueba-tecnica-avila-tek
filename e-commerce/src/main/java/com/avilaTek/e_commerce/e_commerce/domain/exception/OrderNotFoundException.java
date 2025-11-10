package com.avilaTek.e_commerce.e_commerce.domain.exception;

import java.util.UUID;

public class OrderNotFoundException extends DomainException {
    public OrderNotFoundException(Long id){
        super("Order not found with id: " + id);
    }

    public OrderNotFoundException(String id){
        super("Order not found with number: " + id);
    }
}