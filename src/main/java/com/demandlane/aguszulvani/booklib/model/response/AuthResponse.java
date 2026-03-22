package com.demandlane.aguszulvani.booklib.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class AuthResponse {
    public String token;

    public AuthResponse(String token) {
        this.token = token;
    }
}
