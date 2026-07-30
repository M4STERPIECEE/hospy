package com.rdv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicBookingRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String date;
    private String time;
    private String service;
}