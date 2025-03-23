package com.example.cmput301_team_project.utils;

import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.Place;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PlacesUtils {
    public static String getFormattedAddress(Place place) {
        if(place.getAddressComponents() == null) {
            return place.getDisplayName();
        }

        Set<String> addressTypes = new HashSet<>(Arrays.asList("street_number", "route", "locality", "administrative_area_level_1", "country"));

        StringBuilder addressBuilder = new StringBuilder();
        String previousType = "";

        for(AddressComponent component : place.getAddressComponents().asList()) {
            String type = component.getTypes().get(0);

            if(addressTypes.contains(type)) {
                if(addressBuilder.length() > 0) {
                    addressBuilder.append(Objects.equals(previousType, "street_number") ? " " : ", ");
                }
                addressBuilder.append(Objects.equals(type, "country") ? component.getName() : component.getShortName());
                previousType = type;
            }
        }
        return addressBuilder.toString();
    }
}
