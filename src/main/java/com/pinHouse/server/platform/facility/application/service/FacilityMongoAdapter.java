package com.pinHouse.server.platform.facility.application.service;

import com.pinHouse.server.platform.facility.application.usecase.FacilityPort;
import com.pinHouse.server.platform.facility.domain.entity.*;
import com.pinHouse.server.platform.facility.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FacilityMongoAdapter implements FacilityPort {

    /// 외부 의존성
    private final LibraryDocumentRepository libraryRepository;
    private final ParkDocumentRepository parkRepository;
    private final SportDocumentRepository sportRepository;
    private final WalkingDocumentRepository walkingRepository;
    private final AnimalDocumentRepository animalRepository;

    @Override
    public List<Library> loadLibrariesNearBy(double longitude, double latitude, double radiusInKm) {
        return libraryRepository.findByLocation(longitude, latitude,radiusInKm).stream()
                .map(Library::toDomain)
                .toList();
    }

    @Override
    public List<Park> loadParksNearBy(double longitude, double latitude, double radiusInKm) {
        return parkRepository.findByLocation(longitude, latitude,radiusInKm).stream()
                .map(Park::toDomain)
                .toList();
    }

    @Override
    public List<Sport> loadSportsNearBy(double longitude, double latitude, double radiusInKm) {
        return sportRepository.findByLocation(longitude, latitude,radiusInKm).stream()
                .map(Sport::toDomain)
                .toList();
    }

    @Override
    public List<Walking> loadWalkingsNearBy(double longitude, double latitude, double radiusInKm) {
        return walkingRepository.findByLocation(longitude, latitude,radiusInKm).stream()
                .map(Walking::toDomain)
                .toList();
    }

    @Override
    public List<Animal> loadAnimalsNearBy(double longitude, double latitude, double radiusInKm) {
        return animalRepository.findByLocation(longitude, latitude,radiusInKm).stream()
                .map(Animal::toDomain)
                .toList();
    }
}
