package com.ferreteriahogar.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferreteriahogar.api.controller.dto.RegisterRequest;
import com.ferreteriahogar.api.model.User;
import com.ferreteriahogar.api.repository.UserRespository;
import com.ferreteriahogar.api.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserTest {

    @MockitoBean
    private UserRespository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /* -------------------- Helpers -------------------- */

    private User crearUserDummy() {
        User u = new User();
        u.setId(1L);
        u.setUsername("admin");
        u.setPassword("encodedPass");
        u.setRole("ADMIN");
        return u;
    }

    private RegisterRequest crearRegisterRequest() {
        RegisterRequest r = new RegisterRequest();
        r.setUsername("nuevo");
        r.setPassword("password123");
        r.setRole("USER");
        return r;
    }

    /* -------------------- SERVICE -------------------- */

    @Test
    @DisplayName("Servicio - Crear usuario (ok)")
    void testCreateUserServicio_ok() {
        RegisterRequest req = crearRegisterRequest();
        User saved = crearUserDummy();
        saved.setUsername(req.getUsername());

        // username disponible
        when(userRepository.findByUsername(req.getUsername())).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User resultado = userService.createUser(req.getUsername(), req.getPassword(), req.getRole());

        verify(userRepository).findByUsername(req.getUsername());
        verify(userRepository).save(any(User.class));
        assertEquals(req.getUsername(), resultado.getUsername());
        assertEquals("USER", resultado.getRole()); // role normalizado a mayúsculas
    }

    @Test
    @DisplayName("Servicio - Crear usuario (username ya existe)")
    void testCreateUserServicio_usernameExists() {
        RegisterRequest req = crearRegisterRequest();
        when(userRepository.findByUsername(req.getUsername())).thenReturn(crearUserDummy());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(req.getUsername(), req.getPassword(), req.getRole()));

        assertTrue(ex.getMessage().toLowerCase().contains("ya existe") || ex.getMessage().toLowerCase().contains("existe"));
    }

    @Test
    @DisplayName("Servicio - Actualizar usuario (ok)")
    void testUpdateUserServicio_ok() {
        User existing = crearUserDummy();
        existing.setUsername("juan");
        when(userRepository.findByUsername("juan")).thenReturn(existing);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User updated = userService.updateUser("juan", "newpass", "ADMIN");

        verify(userRepository).findByUsername("juan");
        verify(userRepository).save(any(User.class));
        assertEquals("ADMIN", updated.getRole());
        assertNotNull(updated.getPassword()); // se setea (encoded) por el PasswordEncoder del contexto
    }

    @Test
    @DisplayName("Servicio - Eliminar usuario (ok)")
    void testDeleteUserServicio_ok() {
        User u = crearUserDummy();
        when(userRepository.findByUsername("admin")).thenReturn(u);

        assertDoesNotThrow(() -> userService.deleteUser("admin"));
        verify(userRepository).delete(u);
    }

    @Test
    @DisplayName("Servicio - Eliminar usuario (no encontrado)")
    void testDeleteUserServicio_notFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.deleteUser("ghost"));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    /* -------------------- CONTROLLER -------------------- */

    @Test
    @DisplayName("Controlador - GET /users/all (ADMIN)")
    void testGetAllUsersController() throws Exception {
        List<User> lista = Arrays.asList(crearUserDummy(), crearUserDummy());
        when(userRepository.findAll()).thenReturn(lista);

        // endpoint protegido por @PreAuthorize("hasRole('ADMIN')")
        mockMvc.perform(get("/users/all")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("admin"));
    }

    @Test
    @DisplayName("Controlador - POST /users/create-user (ok)")
    void testCreateUserController_ok() throws Exception {
        RegisterRequest req = crearRegisterRequest();
        User saved = crearUserDummy();
        saved.setUsername(req.getUsername());
        saved.setRole(req.getRole().toUpperCase());

        when(userRepository.findByUsername(req.getUsername())).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        mockMvc.perform(post("/users/create-user")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Usuario creado: " + req.getUsername())));
    }

    @Test
    @DisplayName("Controlador - POST /users/create-user (error)")
    void testCreateUserController_error() throws Exception {
        RegisterRequest req = crearRegisterRequest();

        // forzar que el service/repo lance excepción (usuario existe)
        when(userRepository.findByUsername(req.getUsername())).thenReturn(crearUserDummy());

        mockMvc.perform(post("/users/create-user")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Controlador - PUT /users/{username} (ok)")
    void testUpdateUserController_ok() throws Exception {
        RegisterRequest req = crearRegisterRequest();
        User existing = crearUserDummy();
        existing.setUsername("pepito");

        when(userRepository.findByUsername("pepito")).thenReturn(existing);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(put("/users/pepito")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(req.getUsername()));
    }

    @Test
    @DisplayName("Controlador - DELETE /users/{username} (ok)")
    void testDeleteUserController_ok() throws Exception {
        User u = crearUserDummy();
        when(userRepository.findByUsername("toDelete")).thenReturn(u);

        mockMvc.perform(delete("/users/toDelete")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario eliminado con éxito."));
    }

    @Test
    @DisplayName("Controlador - GET /users/me (USER)")
    void testGetProfileController_ok() throws Exception {
        User u = crearUserDummy();
        u.setUsername("diego");
        when(userRepository.findByUsername("diego")).thenReturn(u);

        mockMvc.perform(get("/users/me")
                        .with(SecurityMockMvcRequestPostProcessors.user("diego").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("diego"));
    }
}
