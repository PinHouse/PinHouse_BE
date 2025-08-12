package com.pinHouse.server.platform.application.out.facility;

import com.pinHouse.server.platform.domain.facility.*;

import java.util.List;

public interface FacilityPort {

    List<Library> loadLibrariesNearBy(double longitude, double latitude, double radiusInKm);

    List<Park> loadParksNearBy(double longitude, double latitude, double radiusInKm);

    List<Sport> loadSportsNearBy(double longitude, double latitude, double radiusInKm);

    List<Walking> loadWalkingsNearBy(double longitude, double latitude, double radiusInKm);

    List<Animal> loadAnimalsNearBy(double longitude, double latitude, double radiusInKm);
}
