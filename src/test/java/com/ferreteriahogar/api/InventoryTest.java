package com.ferreteriahogar.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferreteriahogar.api.model.Inventory;
import com.ferreteriahogar.api.model.InventoryProduct;
import com.ferreteriahogar.api.model.Product;
import com.ferreteriahogar.api.model.User;
import com.ferreteriahogar.api.controller.dto.InventoryFullDTO;
import com.ferreteriahogar.api.repository.InventoryProductRepository;
import com.ferreteriahogar.api.repository.InventoryRepository;
import com.ferreteriahogar.api.repository.UserRespository;
import com.ferreteriahogar.api.service.InventoryService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class InventoryTest {

    @MockitoBean
    private InventoryRepository inventoryRepository;

    @MockitoBean
    private InventoryProductRepository inventoryProductRepository;

    @MockitoBean
    private UserRespository userRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    // ------------------------------------
    //      Helpers
    // ------------------------------------

    private User dummyUser() {
        User u = new User();
        u.setUsername("carlos");
        return u;
    }

    private Inventory dummyInventory() {
        Inventory inv = new Inventory();
        inv.setCode("INV001");
        inv.setName("Bodega Central");
        inv.setStatus("ACTIVE");
        inv.setUser(dummyUser());
        return inv;
    }

    private InventoryProduct dummyInvProd(String code, int stock, int minStock) {
        Product p = new Product();
        p.setCode(code);
        p.setName("Producto " + code);

        Inventory inventory = dummyInventory();

        InventoryProduct ip = new InventoryProduct();
        ip.setProduct(p);
        ip.setInventory(inventory);
        ip.setStock(stock);
        ip.setMinStock(minStock);

        return ip;
    }

    // ================================================
    //                TESTS DE SERVICIO
    // ================================================

    @Test
    @DisplayName("Service - Obtener inventario por ID")
    void testGetInventoryById() {
        Inventory inv = dummyInventory();
        when(inventoryRepository.findById("INV001")).thenReturn(Optional.of(inv));

        Inventory res = inventoryService.getInventoryById("INV001");

        assertNotNull(res);
        assertEquals("Bodega Central", res.getName());
    }

    @Test
    @DisplayName("Servicio - Obtener inventario completo")
    void testGetInventoryFull() {

        Inventory inv = dummyInventory();

        List<InventoryProduct> items = Arrays.asList(
                dummyInvProd("P01", 10, 2),
                dummyInvProd("P02", 5, 1)
        );

        when(inventoryRepository.findById("INV001")).thenReturn(Optional.of(inv));
        when(inventoryProductRepository.findByIdInventoryCode("INV001")).thenReturn(items);

        InventoryFullDTO full = inventoryService.getInventoryFull("INV001");

        assertEquals(2, full.getTotalItems());
        assertEquals(15, full.getTotalStock());

        assertEquals("Bodega Central", full.getInventory().getName());

        assertEquals("INV001", full.getInventory().getCode());
        assertEquals("ACTIVE", full.getInventory().getStatus());
        assertEquals("admin", full.getInventory().getUserName());
    }
    @Test
    @DisplayName("Service - Guardar inventario exitosamente")
    void testSaveInventory() {
        Inventory inv = dummyInventory();

        when(userRepository.findByUsername("carlos")).thenReturn(dummyUser());
        when(inventoryRepository.save(inv)).thenReturn(inv);

        Inventory saved = inventoryService.saveInventory(inv);

        assertEquals("Bodega Central", saved.getName());
        verify(inventoryRepository).save(inv);
    }

    @Test
    @DisplayName("Service - Eliminar inventario existente")
    void testDeleteInventory() {
        when(inventoryRepository.existsById("INV001")).thenReturn(true);

        inventoryService.deleteInventory("INV001");

        verify(inventoryRepository).deleteById("INV001");
    }

    // ================================================
    //               TESTS DEL CONTROLLER
    // ================================================

    @Test
    @DisplayName("Controller - GET /inventory")
    void testGetAllController() throws Exception {
        when(inventoryRepository.findAll())
                .thenReturn(Arrays.asList(dummyInventory(), dummyInventory()));

        mockMvc.perform(get("/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Controller - GET /inventory/{code}")
    void testGetByIdController() throws Exception {
        Inventory inv = dummyInventory();
        when(inventoryRepository.findById("INV001")).thenReturn(Optional.of(inv));

        mockMvc.perform(get("/inventory/INV001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bodega Central"));
    }

    @Test
    @DisplayName("Controller - POST /inventory")
    void testCreateInventoryController() throws Exception {
        Inventory inv = dummyInventory();

        when(userRepository.findByUsername("carlos")).thenReturn(dummyUser());
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inv);

        mockMvc.perform(post("/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(inv)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("INV001"));
    }

    @Test
    @DisplayName("Controller - DELETE /inventory/{code}")
    void testDeleteInventoryController() throws Exception {
        when(inventoryRepository.existsById("INV001")).thenReturn(true);

        mockMvc.perform(delete("/inventory/INV001"))
                .andExpect(status().isOk())
                .andExpect(content().string("Inventario eliminado con éxito."));
    }
}
