package com.ferreteriahogar.api.controller.privates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ferreteriahogar.api.model.InventoryProduct;
import com.ferreteriahogar.api.model.InventoryProductId;
import com.ferreteriahogar.api.service.InventoryProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/inventory-product")
@Tag(name = "Inventory Product", description = "Gestión de productos dentro de un inventario")
public class InventoryProductController {

    @Autowired
    private InventoryProductService inventoryProductService;

    // ----------------------- GET LIST BY INVENTORY -----------------------
    @Operation(
            summary = "Obtener todos los productos de un inventario",
            description = "Devuelve la lista de productos asociados al inventario indicado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Código de inventario inválido", content = @Content)
            }
    )
    @GetMapping("/inventory/{inventoryCode}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")

    public ResponseEntity<?> getItemsByInventory(@PathVariable String inventoryCode) {
        try {
            return ResponseEntity.ok(inventoryProductService.getByInventory(inventoryCode));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ----------------------- GET ONE -----------------------
    @Operation(
            summary = "Obtener un item del inventario",
            description = "Devuelve un InventoryProduct según su inventario y código de producto.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Item encontrado"),
                    @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content)
            }
    )
    @GetMapping("/{inventoryCode}/{productCode}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")

    public ResponseEntity<?> getOne(
            @PathVariable String inventoryCode,
            @PathVariable String productCode) {

        try {
            InventoryProductId id = new InventoryProductId(inventoryCode, productCode);
            return ResponseEntity.ok(inventoryProductService.getById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ----------------------- CREATE -----------------------
    @Operation(
            summary = "Crear relación inventario-producto",
            description = "Agrega un producto al inventario especificado.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InventoryProduct.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"inventoryCode\": \"INV001\",\n" +
                                            "  \"productCode\": \"PROD100\",\n" +
                                            "  \"qty\": 5\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Item creado con éxito"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")

    public ResponseEntity<?> create(@RequestBody InventoryProduct ip) {
        try {
            return ResponseEntity.ok(inventoryProductService.save(ip));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ----------------------- SCAN ADD -----------------------
    @Operation(
            summary = "Agregar producto mediante escaneo",
            description = "Incrementa la cantidad de un producto dentro del inventario según cantidad escaneada.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cantidad actualizada correctamente"),
                    @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content)
            }
    )
    @PostMapping("/{inventoryCode}/scan/{productCode}/{qty}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")

    public ResponseEntity<?> addByScan(
            @PathVariable String inventoryCode,
            @PathVariable String productCode,
            @PathVariable Integer qty) {

        try {
            return ResponseEntity.ok(
                    inventoryProductService.addProductByScan(inventoryCode, productCode, qty)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ----------------------- UPDATE -----------------------
    @Operation(
            summary = "Actualizar un item del inventario",
            description = "Modifica la cantidad o la información relacionada a un producto del inventario.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"inventoryCode\": \"INV001\",\n" +
                                            "  \"productCode\": \"PROD100\",\n" +
                                            "  \"qty\": 20\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Item actualizado correctamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
            }
    )
    @PutMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")

    public ResponseEntity<?> update(@RequestBody InventoryProduct ip) {
        try {
            return ResponseEntity.ok(inventoryProductService.update(ip));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ----------------------- DELETE -----------------------
    @Operation(
            summary = "Eliminar un item del inventario",
            description = "Elimina la relación entre un inventario y un producto.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Item eliminado correctamente"),
                    @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content)
            }
    )
    @DeleteMapping("/{inventoryCode}/{productCode}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")

    public ResponseEntity<?> delete(
            @PathVariable String inventoryCode,
            @PathVariable String productCode) {

        try {
            InventoryProductId id = new InventoryProductId(inventoryCode, productCode);
            inventoryProductService.delete(id);
            return ResponseEntity.ok("Item eliminado correctamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
