package com.ferreteriahogar.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ferreteriahogar.api.controller.dto.LoginRequest;
import com.ferreteriahogar.api.controller.dto.LoginResponse;
import com.ferreteriahogar.api.model.User;
import com.ferreteriahogar.api.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder encoder;

    @Autowired
    private com.ferreteriahogar.api.repository.UserRespository userRepository;


    @GetMapping("/status")
    public String status(){
        return "Auth service is running";
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        String token = userService.login(request.getUsername(), request.getPassword());

        if (token == null) return new LoginResponse("error", null);

        return new LoginResponse("ok", token);
    }

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
