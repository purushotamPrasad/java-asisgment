//package com.qssence.backend.authservice.config;
//
//import feign.RequestInterceptor;
//import feign.RequestTemplate;
//import feign.codec.ErrorDecoder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//
//@Configuration
//public class FeignClientConfig {
//
//    @Autowired
//    private OAuth2AuthorizedClientService authorizedClientService;
//
//    @Bean
//    public ErrorDecoder errorDecoder() {
//        return new ErrorDecoder.Default();
//    }
//    @Bean
//    public RequestInterceptor oauth2FeignRequestInterceptor() {
//        return new RequestInterceptor() {
//            @Override
//            public void apply(RequestTemplate template) {
//                // Fetch the token for the current client
//                String accessToken = getAccessToken();
//                if (accessToken != null) {
//                    template.header("Authorization", "Bearer " + accessToken);
//                }
//            }
//        };
//    }
//
//
//
//    private String getAccessToken() {
//        // Get the principal name from the authenticated user's context
//        String principalName = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        // Use the OAuth2AuthorizedClientService to fetch the authorized client
//        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
//                "keycloak", principalName);  // Use "keycloak" as the client ID from the YAML file and the principal name
//
//        if (authorizedClient != null) {
//            // Return the access token if available
//            return authorizedClient.getAccessToken().getTokenValue();
//        }
//
//        // Return null if no authorized client is found
//        return null;
//    }
//
//}
