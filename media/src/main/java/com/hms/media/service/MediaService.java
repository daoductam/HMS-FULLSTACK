package com.hms.media.service;

import com.hms.media.dto.MediaFileDTO;
import com.hms.media.entity.MediaFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface MediaService {
    MediaFileDTO storeFile(MultipartFile file) throws IOException;
    Optional<MediaFile> getFile(Long id);
}
