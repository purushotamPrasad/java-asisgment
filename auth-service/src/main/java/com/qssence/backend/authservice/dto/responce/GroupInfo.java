package com.qssence.backend.authservice.dto.responce;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupInfo {

    private Long groupId;
    private String groupName;
    private String status;


}
