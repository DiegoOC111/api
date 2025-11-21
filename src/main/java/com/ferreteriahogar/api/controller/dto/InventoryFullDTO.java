package com.ferreteriahogar.api.controller.dto;

import java.util.List;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryFullDTO {

    private InventoryInfo inventory;
    private Integer totalItems;   
    private Integer totalStock;   

    private List<ProductInInventoryDTO> products;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InventoryInfo {
        private String code;
        private String name;
        private String status;
        private String userName;
    }
}