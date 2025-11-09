package com.avilaTek.e_commerce.e_commerce.domain.exception;

public class ProductNotFounfException extends DomainException{
    public ProductNotFounfException(Long id) {
        super("Product not found with id: " + id);
    }
}
