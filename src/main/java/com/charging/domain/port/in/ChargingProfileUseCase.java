package com.charging.domain.port.in;

import com.charging.domain.entity.ChargingProfile;
import com.charging.domain.enums.ChargingProfileKindEnum;
import com.charging.domain.enums.ChargingProfilePurposeEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ChargingProfileUseCase {
    List<ChargingProfile> getProfilesByStationId(String stationId);
    List<ChargingProfile> getActiveProfiles(String stationId);
    ChargingProfile createProfile(String stationId, Integer evseId, Integer stackLevel,
            ChargingProfilePurposeEnum purpose, ChargingProfileKindEnum kind,
            String chargingRateUnit, LocalDateTime validFrom, LocalDateTime validTo,
            BigDecimal minChargingRate, BigDecimal maxDischargePower, BigDecimal minDischargePower,
            String dischargeRateUnit);
    ChargingProfile updateProfile(Integer chargingProfileId, LocalDateTime validFrom, LocalDateTime validTo,
            BigDecimal minChargingRate, BigDecimal maxDischargePower, BigDecimal minDischargePower);
    void deactivateProfile(Integer chargingProfileId);
}
