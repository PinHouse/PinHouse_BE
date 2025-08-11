package com.pinHouse.server.platform.adapter.out;

import com.pinHouse.server.platform.adapter.out.mongo.facility.LibraryDocument;
import com.pinHouse.server.platform.adapter.out.mongo.facility.LibraryDocumentRepository;
import com.pinHouse.server.platform.application.out.facility.LibraryPort;
import com.pinHouse.server.platform.domain.facility.Library;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LibraryMongoAdapter implements LibraryPort {

    private final LibraryDocumentRepository repository;

    @Override
    public List<Library> loadLibrariesNearBy(double longitude, double latitude, double radiusInKm) {
        return repository.findByLocation(longitude, latitude,radiusInKm).stream()
                .map(LibraryDocument::toDomain)
                .toList();
    }
}
