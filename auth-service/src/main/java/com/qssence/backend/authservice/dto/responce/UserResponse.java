package com.qssence.backend.authservice.dto.responce;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String id;
    private String email;
    private String fullName;
    private String username;
    private Boolean status;
    private String role;

    public UserResponse(String id, String firstName, String lastName, String email) {
    }
}
