package com.ferreteriahogar.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ferreteriahogar.api.model.User;
import com.ferreteriahogar.api.repository.UserRespository;

@Service
public class UserService {
    
    @Autowired
    private UserRespository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder encoder;



    public String login(String username, String password){
        User user = userRepository.findByUsername(username);

        if (user == null) return null;
        if (!encoder.matches(password, user.getPassword())) return null;

        return jwtService.generateToken(user);
    }

    public String getRole(String username){
        User user = userRepository.findByUsername(username);
        return user.getRole();
    }

    public User getByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        userRepository.delete(user);
    }

    public User updateUser(String username, String password, String role,String NewUsername) {

        User existing = getByUsername(username);

        if (username != null && !username.trim().isEmpty()) {
            if (!existing.getUsername().equals(NewUsername)) {
                checkUsernameAvailable(NewUsername);
                existing.setUsername(NewUsername);

            }else{
                existing.setUsername(username);

            }
        }

        if (password != null && !password.trim().isEmpty()) {
            existing.setPassword(encoder.encode(password));
        }
        List<String> rolesValidos = List.of("USER", "ADMIN", "UADMIN", "IADMIN");
        if (role != null && !role.trim().isEmpty()) {
            String r = role.toUpperCase();
            if (!rolesValidos.contains(r)) {
                throw new IllegalArgumentException("Rol inválido. Debe ser USER o ADMIN");
            }
            existing.setRole(r);
        }

        return userRepository.save(existing);
    }

    public User createUser(String username, String password, String role) {

        validateUserFields(username, password, role);
        checkUsernameAvailable(username);

        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        user.setRole(role.toUpperCase());

        return userRepository.save(user);
    }

    private void validateUserFields(String username, String password, String role) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");

        if (password == null || password.trim().isEmpty())
            throw new IllegalArgumentException("La contraseña es obligatoria.");

        if (role == null || role.trim().isEmpty())
            throw new IllegalArgumentException("El rol es obligatorio.");

        String normalizedRole = role.toUpperCase();
        if (!normalizedRole.equals("USER") && !normalizedRole.equals("ADMIN") && !normalizedRole.equals("UADMIN") && !normalizedRole.equals("IADMIN"))
            throw new IllegalArgumentException("El rol debe ser 'USER' o 'ADMIN' O IADMIN O UADMIN.");
    }

    private void checkUsernameAvailable(String username) {
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("El usuario ya existe.");
        }
    }
}
