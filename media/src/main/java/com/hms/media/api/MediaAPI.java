package com.hms.media.api;

import com.hms.media.dto.MediaFileDTO;
import com.hms.media.entity.MediaFile;
import com.hms.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaAPI {

    private final MediaService mediaService;

    @PostMapping("/upload")
    public ResponseEntity<MediaFileDTO> uploadFile(@RequestParam("file")MultipartFile file) {
        try {
            MediaFileDTO mediaFileDTO = mediaService.storeFile(file);
            return ResponseEntity.ok(mediaFileDTO);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) {
        Optional<MediaFile> mediaFileOptional = mediaService.getFile(id);
        if (mediaFileOptional.isPresent()) {
            MediaFile mediaFile = mediaFileOptional.get();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                            mediaFile.getName() + "\"")
                    .contentType(MediaType.parseMediaType(mediaFile.getType()))
                    .body(mediaFile.getData());
        } else  {
            return  ResponseEntity.notFound().build();
        }
    }
}
