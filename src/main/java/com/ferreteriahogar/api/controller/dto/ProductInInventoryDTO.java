package com.ferreteriahogar.api.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInInventoryDTO {
    private String code;
    private String name;
    private Integer stock;
    private Integer minStock;
}
