package com.charging.adapter.in.web;

import com.charging.adapter.in.web.dto.ChargingProfileRequest;
import com.charging.adapter.in.web.dto.ChargingProfileResponse;
import com.charging.domain.entity.ChargingProfile;
import com.charging.domain.enums.ChargingProfileKindEnum;
import com.charging.domain.enums.ChargingProfilePurposeEnum;
import com.charging.domain.port.in.ChargingProfileUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/charging-profiles")
@RequiredArgsConstructor
public class ChargingProfileController {

    private final ChargingProfileUseCase chargingProfileUseCase;

    @GetMapping
    public ResponseEntity<List<ChargingProfileResponse>> getProfiles(
            @RequestParam String stationId,
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        List<ChargingProfile> profiles = activeOnly
                ? chargingProfileUseCase.getActiveProfiles(stationId)
                : chargingProfileUseCase.getProfilesByStationId(stationId);
        List<ChargingProfileResponse> responses = profiles.stream()
                .map(ChargingProfileResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<ChargingProfileResponse> createProfile(@RequestBody ChargingProfileRequest request) {
        ChargingProfile profile = chargingProfileUseCase.createProfile(
                request.stationId(),
                request.evseId(),
                request.stackLevel(),
                ChargingProfilePurposeEnum.valueOf(request.chargingProfilePurpose()),
                ChargingProfileKindEnum.valueOf(request.chargingProfileKind()),
                request.chargingRateUnit(),
                request.validFrom(),
                request.validTo(),
                request.minChargingRate(),
                request.maxDischargePower(),
                request.minDischargePower(),
                request.dischargeRateUnit()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ChargingProfileResponse.from(profile));
    }

    @PutMapping("/{chargingProfileId}")
    public ResponseEntity<ChargingProfileResponse> updateProfile(
            @PathVariable Integer chargingProfileId,
            @RequestBody ChargingProfileRequest request) {
        ChargingProfile profile = chargingProfileUseCase.updateProfile(
                chargingProfileId,
                request.validFrom(),
                request.validTo(),
                request.minChargingRate(),
                request.maxDischargePower(),
                request.minDischargePower()
        );
        return ResponseEntity.ok(ChargingProfileResponse.from(profile));
    }

    @DeleteMapping("/{chargingProfileId}")
    public ResponseEntity<Void> deactivateProfile(@PathVariable Integer chargingProfileId) {
        chargingProfileUseCase.deactivateProfile(chargingProfileId);
        return ResponseEntity.noContent().build();
    }
}
