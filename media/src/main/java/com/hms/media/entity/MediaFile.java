package com.hms.media.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Getter
@Setter
public class MediaFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  String name;
    private String type;
    private Long size;
    @Lob
    private byte[] data;
    private Storage storage;

    @CreationTimestamp
    private LocalDateTime createdAt;

//    public MediaFile setName(String name) {
//        this.name = name;
//        return this;
//    }
//
//    public MediaFile setType(String type) {
//        this.type = type;
//        return this;
//    }
//
//    public MediaFile build() {
//        return this;
//    }
//
//    public  void getObj() {
//        MediaFile.builder()
//                .name("Jdjd")
//                .type("image/png")
//                .build();
//    }
}
