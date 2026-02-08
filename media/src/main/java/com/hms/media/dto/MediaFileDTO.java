package com.hms.media.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MediaFileDTO {
    private  Long id;
    private  String name;
    private String type;
    private Long size;
}
