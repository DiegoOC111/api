package com.ferreteriahogar.api.controller.privates;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;

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

import com.ferreteriahogar.api.model.Product;
import com.ferreteriahogar.api.service.ProductService;

@RestController
@RequestMapping("/products")
@Tag(name = "Product", description = "Gestión de productos")
public class ProductController {

    @Autowired
    private ProductService productService;

    // ============================
    // GET ALL
    // ============================
    @Operation(
            summary = "Obtener todos los productos",
            description = "Retorna una lista completa de productos disponibles en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Product.class))))
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    // ============================
    // GET BY ID
    // ============================
    @Operation(
            summary = "Obtener producto por código",
            description = "Devuelve un producto según el código proporcionado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Código inválido o producto no encontrado",
                    content = @Content)
    })
    @GetMapping("/{code}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> getById(@PathVariable String code) {
        try {
            return ResponseEntity.ok(productService.getById(code));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ============================
    // CREATE
    // ============================
    @Operation(
            summary = "Crear un nuevo producto",
            description = "Registra un nuevo producto en el inventario."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos para crear el producto")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<?> create(@RequestBody Product p) {
        try {
            return ResponseEntity.ok(productService.create(p));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ============================
    // UPDATE
    // ============================
    @Operation(
            summary = "Actualizar un producto",
            description = "Modifica los datos de un producto existente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos para actualizar")
    })
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<?> update(@RequestBody Product p, @PathVariable String OldNAme) {
        try {
            return ResponseEntity.ok(productService.update(p, OldNAme));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ============================
    // DELETE
    // ============================
    @Operation(
            summary = "Eliminar un producto",
            description = "Elimina un producto del sistema utilizando su código."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Producto no encontrado o error al eliminar")
    })
    @DeleteMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<?> delete(@PathVariable String code) {
        try {
            productService.delete(code);
            return ResponseEntity.ok("Producto eliminado con éxito.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
