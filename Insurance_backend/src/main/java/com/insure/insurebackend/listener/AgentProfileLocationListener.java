package com.insure.insurebackend.listener;

import com.insure.insurebackend.config.SpringContext;
import com.insure.insurebackend.model.AgentProfile;
import com.insure.insurebackend.service.Location;
import com.insure.insurebackend.service.LocationService;
import org.springframework.util.StringUtils;

public class AgentProfileLocationListener {

    public void populateCoordinates(AgentProfile profile) {
        if (profile == null) {
            return;
        }
        if (!StringUtils.hasText(profile.getPincode())) {
            return;
        }
        if (profile.getLatitude() != null || profile.getLongitude() != null) {
            return;
        }

        try {
            LocationService locationService = SpringContext.getBean(LocationService.class);
            Location location = locationService.getCoordinatesFromPincode(profile.getPincode());
            if (location == null) {
                return;
            }

            profile.setLatitude(location.getLatitude());
            profile.setLongitude(location.getLongitude());
        } catch (Exception ignored) {
            // Leave coordinates null when geocoding is unavailable.
        }
    }
}
