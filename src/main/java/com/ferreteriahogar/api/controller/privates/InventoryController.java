package com.ferreteriahogar.api.controller.privates;

import io.swagger.v3.oas.annotations.tags.Tag;
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
import com.ferreteriahogar.api.model.Inventory;
import com.ferreteriahogar.api.service.InventoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/inventory")
@Tag(name = "Inventory", description = "Gestión de inventarios")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;


    @Operation(
            summary = "Obtener todos los inventarios",
            description = "Devuelve una lista completa de inventarios registrados en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Inventory.class))))
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")

    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }


    @Operation(
            summary = "Obtener inventario por código",
            description = "Devuelve la información del inventario asociado al código enviado como parámetro."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario obtenido correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Inventory.class))),
            @ApiResponse(responseCode = "400", description = "Código inválido o inventario no encontrado",
                    content = @Content)
    })
    @GetMapping("/{code}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")

    public ResponseEntity<?> getById(@PathVariable String code) {
        try {
            return ResponseEntity.ok(inventoryService.getInventoryById(code));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @Operation(
            summary = "Obtener inventario completo",
            description = "Devuelve la información del inventario junto con sus productos asociados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario completo obtenido correctamente"),
            @ApiResponse(responseCode = "400", description = "Código inválido o inventario no encontrado",
                    content = @Content)
    })
    @GetMapping("/{inventoryCode}/full")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")

    public ResponseEntity<?> getFullInventory(@PathVariable String inventoryCode) {
        try {
            return ResponseEntity.ok(inventoryService.getInventoryFull(inventoryCode));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @Operation(
            summary = "Crear un nuevo inventario",
            description = "Crea un inventario con la información proporcionada."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario creado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Inventory.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos para la creación",
                    content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<?> create(@RequestBody Inventory inventory) {
        try {
            Inventory saved = inventoryService.saveInventory(inventory);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @Operation(
            summary = "Actualizar inventario",
            description = "Actualiza los datos del inventario enviado en el cuerpo de la petición."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario actualizado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Inventory.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o inventario inexistente",
                    content = @Content)
    })
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<?> update(@RequestBody Inventory inventory) {
        try {
            Inventory updated = inventoryService.updateInventory(inventory);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @Operation(
            summary = "Eliminar inventario",
            description = "Elimina el inventario asociado al código proporcionado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario eliminado correctamente"),
            @ApiResponse(responseCode = "400", description = "Código inválido o inventario no encontrado",
                    content = @Content)
    })
    @DeleteMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<?> delete(@PathVariable String code) {
        try {
            inventoryService.deleteInventory(code);
            return ResponseEntity.ok("Inventario eliminado con éxito.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
