package paa.locker.business;

import paa.locker.model.Locker;


import paa.locker.model.Parcel;


import java.time.LocalDate;
import java.util.List;

public interface ParcelService {
    Locker createLocker(String name, String address, double longitude, double latitude, int largeCompartments, int smallCompartments);
    Locker findLocker(Long lockerCode);
    List<Locker> findAllLockers();
    int availableCompartments(Long lockerCode, LocalDate date, float parcelWeight);
    Parcel deliverParcel(Long lockerCode, int addressee, float weight, LocalDate arrivalDate);
    void retrieveParcel(Long parcelCode);
    

    
    float SmallMaxWeight  = 1.0f;  // S parcels will be in the range 0.0             < weight <= SmallMaxWeight
    float LargeMaxWeight  = 10.0f; // L parcels will be in the range MediumMaxWeight < weight <= LargeMaxWeight
    int   MaxDaysInLocker = 3;     // Parcels will be removed after MaxDaysInLocker days in the locker and their compartment reused
    int   MaxParcelsInLocker = 3;  // A given addressee cannot have more than MaxParcelsInLocker compartments used in a given locker at any time
    int   MaxParcelsAnywhere = 5;  // A given addressee cannot have more than MaxParcelsAnywhere compartments used across all lockers at any time

  
    

}
