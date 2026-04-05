package org.example.rawabet.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.RegisterRequest;
import org.example.rawabet.dto.UserResponse;
import org.example.rawabet.services.IService.user.IUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    // ✅ REGISTER (client)
    @PostMapping("/add")
    public UserResponse addUser(@Valid @RequestBody RegisterRequest request) {
        return userService.addUser(request);
    }

    // ✅ CREATE WITH ROLE (SUPER_ADMIN only)
    @PostMapping("/add-with-role")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public UserResponse addUserWithRole(@Valid @RequestBody RegisterRequest request) {
        return userService.addUserWithRole(request);
    }

    // ✅ UPDATE
    @PutMapping("/update")
    public UserResponse updateUser(@Valid @RequestBody RegisterRequest request,
                                   @RequestParam Long id) {
        return userService.updateUser(id, request);
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    // GET BY ID
    @GetMapping("/get/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // GET ALL
    @GetMapping("/all")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }
}