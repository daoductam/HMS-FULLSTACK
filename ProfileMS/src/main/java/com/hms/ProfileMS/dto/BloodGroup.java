package com.hms.ProfileMS.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum BloodGroup {
    @JsonProperty("A+") A_POSITIVE,
    @JsonProperty("A-") A_NEGATIVE,
    @JsonProperty("B+") B_POSITIVE,
    @JsonProperty("B-") B_NEGATIVE,
    @JsonProperty("O+") O_POSITIVE,
    @JsonProperty("O-") O_NEGATIVE,
    @JsonProperty("AB+") AB_POSITIVE,
    @JsonProperty("AB-") AB_NEGATIVE

}
