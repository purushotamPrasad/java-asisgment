package com.qssence.backend.document_initiation_service.exeptionHandler;

public class DocumentNotFoundException extends RuntimeException {

    public DocumentNotFoundException(String message) {
        super(message);
    }
}
