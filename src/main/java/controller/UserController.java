package controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import util.MappingUtil;

import static org.springframework.http.HttpStatus.CREATED;
import entity.UserEntity;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final MappingUtil mappingUtil;

    @GetMapping
    @PreAuthorize("hasAuthority('read')")
    public ResponseEntity<User> getUser(String name) {
        UserEntity entity = userService.getUser(name);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mappingUtil.toDto(entity));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> addUser(
            @Valid @RequestBody User user){
        UserEntity savedEntity = userService.addUser(user);
        return ResponseEntity.status(CREATED)
                .header("Name", user.getName())
                .body(mappingUtil.toDto(savedEntity));
    }

    @DeleteMapping("by-name/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeUser(@PathVariable String name) {
        userService.removeUser(name);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(
            @PathVariable String name,
            @RequestBody User updatedFields) {

        UserEntity updated = userService.updateUser(name, updatedFields);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mappingUtil.toDto(updated));
    }
}
