package com.qssence.backend.document_initiation_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void apply(RequestTemplate template) {
        // Get the current request attributes
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        // Check if the request attributes are available
        if (attributes != null) {
            // Get the original HttpServletRequest from the user
            HttpServletRequest request = attributes.getRequest();
            
            // Get the Authorization header from the original request
            String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
            
            // If the header exists, add it to the outgoing Feign request
            if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
                template.header(AUTHORIZATION_HEADER, authorizationHeader);
            }
        }
    }
} 