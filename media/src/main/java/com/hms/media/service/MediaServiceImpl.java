package com.hms.media.service;

import com.hms.media.dto.MediaFileDTO;
import com.hms.media.entity.MediaFile;
import com.hms.media.entity.Storage;
import com.hms.media.repository.MediaFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService{
    private final MediaFileRepository mediaFileRepository;
    @Override
    public MediaFileDTO storeFile(MultipartFile file) throws IOException {
        MediaFile mediaFile = MediaFile.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .data(file.getBytes())
                .storage(Storage.DB).build();

        MediaFile savedFile = mediaFileRepository.save(mediaFile);
        return MediaFileDTO.builder()
                .id(savedFile.getId())
                .name(savedFile.getName())
                .type(savedFile.getType())
                .size(savedFile.getSize())
                .build();
    }

    @Override
    public Optional<MediaFile> getFile(Long id) {
        return mediaFileRepository.findById(id);
    }
}
