package com.qssence.backend.document_initiation_service.service.impl;

import com.qssence.backend.document_initiation_service.client.AuthServiceClient;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class AccessControlService {

    private final AuthServiceClient authServiceClient;

    public AccessControlService(AuthServiceClient authServiceClient) {

        this.authServiceClient = authServiceClient;
    }

//    /**
//     * Check if a user has permission for a specific action on a document.
//     *
//     * @param userId - ID of the user performing the action
//     * @param documentId - ID of the document
//     * @param action - Action to be checked (VIEW, EDIT, COMMENT, INITIATE)
//     * @return true if user has permission, false otherwise
//     */
//    public boolean hasPermission(Long userId, Long documentId, String action) {
//        List<String> userPermissions = (List<String>) authServiceClient.checkPermission(userId,documentId,action);
//        return userPermissions.contains(action);
//    }

    public void checkPermission(Long userId, String permission) {
        Boolean hasPermission = authServiceClient.checkPermission(userId, permission);
        if (!Boolean.TRUE.equals(hasPermission)) {
            throw new AccessDeniedException("User does not have permission: " + permission);
        }
    }
}
