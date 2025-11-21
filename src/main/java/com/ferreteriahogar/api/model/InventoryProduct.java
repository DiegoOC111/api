package com.ferreteriahogar.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "inventory_product") 
@NoArgsConstructor
@AllArgsConstructor
public class InventoryProduct {

    @EmbeddedId
    private InventoryProductId id;

    @ManyToOne
    @MapsId("inventoryCode")
    @JoinColumn(name = "inventory_id", nullable = false, foreignKey = @ForeignKey(name = "fk_invprod_inventory"))
    @JsonIgnore
    private Inventory inventory; 

    @ManyToOne
    @MapsId("productCode")
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_invprod_product"))
    @JsonIgnore
    private Product product;       

    @Column(nullable = false)
    private Integer stock;         

    @Column(nullable = false)
    private Integer minStock;
}
