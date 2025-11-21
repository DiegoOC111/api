package com.ferreteriahogar.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ferreteriahogar.api.controller.dto.LoginRequest;
import com.ferreteriahogar.api.controller.dto.LoginResponse;
import com.ferreteriahogar.api.model.User;
import com.ferreteriahogar.api.service.UserService;

@RestController
@RequestMapping("/auth")
@Tag(
        name = "Auth",
        description = "Endpoints de autenticación, login y creación de administrador."
)
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder encoder;

    @Autowired
    private com.ferreteriahogar.api.repository.UserRespository userRepository;

    // ============================================
    // STATUS
    // ============================================
    @Operation(
            summary = "Verificar estado del servicio de autenticación",
            description = "Devuelve un mensaje simple indicando que el servicio está activo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Servicio funcionando correctamente")
    })
    @GetMapping("/status")
    public String status() {
        return "Auth service is running";
    }

    // ============================================
    // LOGIN
    // ============================================
    @Operation(
            summary = "Iniciar sesión",
            description = "Recibe un usuario y contraseña, valida las credenciales y devuelve un token JWT si son correctas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login exitoso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Login correcto",
                                            value = """
                        {
                          "status": "ok",
                          "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                        }
                        """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Credenciales inválidas",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Login incorrecto",
                                            value = """
                        {
                          "status": "error",
                          "token": null
                        }
                        """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Credenciales del usuario",
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Petición de ejemplo",
                                            value = """
                        {
                          "username": "Seba",
                          "password": "abc123"
                        }
                        """
                                    )
                            }
                    )
            )
            LoginRequest request
    ) {

        String token = userService.login(request.getUsername(), request.getPassword());

        if (token == null) return new LoginResponse("error", null);

        return new LoginResponse("ok", token);
    }

    // ============================================
    // BOOTSTRAP ADMIN
    // ============================================
    @Operation(
            summary = "Crear un administrador inicial",
            description = "Crea un usuario ADMIN por defecto en la base de datos. Se recomienda usar solo una vez."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrador creado exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno al crear el administrador")
    })
    @PostMapping("/bootstrap-admin")
    public ResponseEntity<?> bootstrapAdmin() {

        User admin = new User();
        admin.setUsername("Seba");
        admin.setPassword(encoder.encode("abc123"));
        admin.setRole("ADMIN");

        userRepository.save(admin);

        return ResponseEntity.ok("Admin creado");
    }
}
