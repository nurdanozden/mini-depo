package com.minidepo.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(int id) {
        super("Ürün bulunamadı! ID: " + id);
    }

    public ProductNotFoundException(String name) {
        super("Ürün bulunamadı! İsim: " + name);
    }
}

