package com.qssence.backend.authservice.service;

import com.qssence.backend.authservice.dto.request.LoginRequest;
import com.qssence.backend.authservice.dto.request.LogoutReq;

/**
 * Interface for user authentication service.
 * Defines methods for user login and logout operations.
 */
public interface IAuthService {

    /**
     * Authenticates a user by verifying the provided credentials.
     *
     * @param loginRequest The username of the user attempting to log in.
     * @return Something not yet decided
     */
    Object login(LoginRequest loginRequest);

    /**
     * Logs out a currently authenticated user.
     *
     * @param logoutReq The username of the user to log out.
     * @return Something
     */
    Object logout(LogoutReq logoutReq);
}
