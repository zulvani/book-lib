package com.demandlane.aguszulvani.booklib.controller;

import com.demandlane.aguszulvani.booklib.model.request.AuthRequest;
import com.demandlane.aguszulvani.booklib.model.response.AuthResponse;
import com.demandlane.aguszulvani.booklib.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This auth controller is responsible for handling authentication requests.
 * Please don't use this at production, this is just for testing purposes.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Value( "${user.guest.username}")
    String guestUserName;

    @Value( "${user.guest.password}")
    String guestUserPassword;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {

        if (guestUserName.equals(request.username) && guestUserPassword.equals(request.password)) {

            String token = jwtService.generateToken(request.username, "ROLE_GUEST");
            return ResponseEntity.ok(new AuthResponse(token));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}
