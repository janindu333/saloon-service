package com.baber.saloonservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffInviteResponse {
    private String invitationId;
    private String message;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String status;
}
