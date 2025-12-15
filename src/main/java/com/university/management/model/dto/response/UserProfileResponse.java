package com.university.management.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    @JsonIgnore
    private Long id;

    private String username;
    private String role;
    private Object details;
}
