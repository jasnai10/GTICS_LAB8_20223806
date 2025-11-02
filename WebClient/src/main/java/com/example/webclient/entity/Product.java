package com.example.webclient.entity;

import lombok.*;

@Getter
@Setter
public class Product {
    private Integer id;
    private String name;
    private String unit;
    private Long price;
    private Supplier supplier;
    private Category category;
}