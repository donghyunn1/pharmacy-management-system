// 양동현. 2025.06.18
package com.example.pharmacy.customer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CustomerSearchDto {
    private String searchQuery = "";
    private String searchBy = "name"; // name, phone, email
    private Boolean isMember; // null, true, false
}