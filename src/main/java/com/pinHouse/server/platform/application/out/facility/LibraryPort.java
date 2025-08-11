package com.pinHouse.server.platform.application.out.facility;

import com.pinHouse.server.platform.domain.facility.Library;

import java.util.List;

public interface LibraryPort {

    List<Library> loadLibrariesNearBy(double longitude, double latitude, double radiusInKm);


}
