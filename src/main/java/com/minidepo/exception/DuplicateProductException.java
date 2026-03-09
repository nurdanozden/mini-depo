package com.minidepo.exception;

public class DuplicateProductException extends RuntimeException {

    public DuplicateProductException(String name) {
        super("Bu isimde bir ürün zaten mevcut: " + name);
    }
}

