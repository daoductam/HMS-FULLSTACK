package com.hms.media.service;

import com.hms.media.dto.MediaFileDTO;
import com.hms.media.entity.MediaFile;

import java.io.IOException;
import java.util.Optional;

public interface MediaService {
    MediaFileDTO storeFile(byte[] data, String fileName, String contentType) throws IOException;
    Optional<MediaFile> getFile(Long id);
}
