package com.hms.media.service;

import com.hms.media.dto.MediaFileDTO;
import com.hms.media.entity.MediaFile;
import com.hms.media.entity.Storage;
import com.hms.media.repository.MediaFileRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {
    
    private final MediaFileRepository mediaFileRepository;

    @Override
    @Transactional
    public MediaFileDTO storeFile(byte[] data, String fileName, String contentType) throws IOException {
        MediaFile mediaFile = MediaFile.builder()
                .name(fileName)
                .type(contentType)
                .data(data)
                .size((long) data.length)
                .storage(Storage.DB)
                .build();

        mediaFileRepository.persist(mediaFile);
        
        return MediaFileDTO.builder()
                .id(mediaFile.getId())
                .name(mediaFile.getName())
                .type(mediaFile.getType())
                .size(mediaFile.getSize())
                .build();
    }

    @Override
    public Optional<MediaFile> getFile(Long id) {
        return mediaFileRepository.findByIdOptional(id);
    }
}
