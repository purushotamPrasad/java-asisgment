//package com.qssence.backend.authservice.config;
//
//import com.qssence.backend.authservice.dto.Plants.CompanyResponse;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//@FeignClient(name = "subscription-panel", url = "http://localhost:8085")
//public interface CompanyFeignClient  {
//    @GetMapping("/api/v2/company/getById/{id}")
//    CompanyResponse getCompanyById(@PathVariable("id") String companyId);
//}
