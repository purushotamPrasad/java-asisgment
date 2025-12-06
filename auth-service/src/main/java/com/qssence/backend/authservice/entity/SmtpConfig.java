package com.qssence.backend.authservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Data
@Table(name="mail-config")
public class SmtpConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String providerName; // e.g., "Gmail", "Yahoo", "CustomSMTP"
    private String mailHost;
    private int mailPort;
    private String mailUsername;
    private String mailPassword;
    private String mailProtocol;
    private boolean mailAuth;
    private boolean mailStarttlsEnable;


}
