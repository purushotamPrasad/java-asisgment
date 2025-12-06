//package com.qssence.backend.authservice.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.util.Date;
//
//@Setter
//@Getter
//@Entity
//@Table(name="email")
//public class Email {
//
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String providerName;
//    private String recipient;
//    private String subject;
//
//    @Lob
//    private String body;
//
//    private boolean isHtml;
//    private String status;
//    private String errorMessage;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date sentAt;
//
//}
