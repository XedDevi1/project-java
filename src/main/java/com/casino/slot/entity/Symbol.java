package com.casino.slot.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "symbols")
@Data
public class Symbol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;        // например "Cherry", "Seven", "Wild"

    private String imageUrl;

    private Integer value;      // условная ценность для сортировки
}