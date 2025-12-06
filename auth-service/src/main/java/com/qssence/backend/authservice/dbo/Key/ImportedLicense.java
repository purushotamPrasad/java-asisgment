package com.qssence.backend.authservice.dbo.Key;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class ImportedLicense  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long licenseId;        // From subscription-panel
    private String licenseKey;     // Base64 encoded key

    private String companyId;
    private String companyName;
    private String companyPrefix;  // ✅ add for tenant separation if needed
    private String email;
    private String location;

    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private String description;

    private Long purchaseCost;
    private Long totalUserAccess;
    private Long adminAccountAllowed;
    private Long userAccountAllowed;

    private boolean active;        // ✅ track active/inactive
    private boolean expired;       // ✅ auto-computed after expiry date

    private LocalDateTime importedAt;    // ✅ when license imported
    private LocalDateTime lastVerifiedAt; // ✅ when last verified with subscription-panel

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Plan> plans;
}
