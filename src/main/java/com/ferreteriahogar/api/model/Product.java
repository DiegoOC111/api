package com.ferreteriahogar.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @Column(nullable = false, unique = true)
    private String code;  

    @Column(nullable = false)
    private String name;  

    @Column(nullable = false)
    private String description; 

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<InventoryProduct> inventoryProducts;
}
