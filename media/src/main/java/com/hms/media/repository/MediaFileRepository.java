package com.hms.media.repository;

import com.hms.media.entity.MediaFile;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MediaFileRepository implements PanacheRepository<MediaFile> {
}
