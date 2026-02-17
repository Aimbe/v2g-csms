package com.charging.application.service;

import com.charging.domain.entity.ChargingProfile;
import com.charging.domain.enums.ChargingProfileKindEnum;
import com.charging.domain.enums.ChargingProfilePurposeEnum;
import com.charging.domain.exception.ResourceNotFoundException;
import com.charging.domain.port.in.ChargingProfileUseCase;
import com.charging.domain.port.out.ChargingProfilePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChargingProfileServiceImpl implements ChargingProfileUseCase {

    private final ChargingProfilePort chargingProfilePort;

    @Override
    public List<ChargingProfile> getProfilesByStationId(String stationId) {
        return chargingProfilePort.findByStationId(stationId);
    }

    @Override
    public List<ChargingProfile> getActiveProfiles(String stationId) {
        return chargingProfilePort.findActiveProfilesByStationId(stationId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public ChargingProfile createProfile(String stationId, Integer evseId, Integer stackLevel,
            ChargingProfilePurposeEnum purpose, ChargingProfileKindEnum kind,
            String chargingRateUnit, LocalDateTime validFrom, LocalDateTime validTo,
            BigDecimal minChargingRate, BigDecimal maxDischargePower, BigDecimal minDischargePower,
            String dischargeRateUnit) {
        log.info("충전 프로파일 생성 요청: stationId={}, stackLevel={}", stationId, stackLevel);

        int chargingProfileId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);

        ChargingProfile profile = ChargingProfile.builder()
                .chargingProfileId(chargingProfileId)
                .stationId(stationId)
                .evseId(evseId)
                .stackLevel(stackLevel)
                .chargingProfilePurpose(purpose)
                .chargingProfileKind(kind)
                .chargingRateUnit(chargingRateUnit)
                .validFrom(validFrom)
                .validTo(validTo)
                .minChargingRate(minChargingRate)
                .maxDischargePower(maxDischargePower)
                .minDischargePower(minDischargePower)
                .dischargeRateUnit(dischargeRateUnit)
                .build();

        ChargingProfile saved = chargingProfilePort.save(profile);
        log.info("충전 프로파일 생성 완료: chargingProfileId={}", saved.getChargingProfileId());
        return saved;
    }

    @Override
    @Transactional
    public ChargingProfile updateProfile(Integer chargingProfileId, LocalDateTime validFrom, LocalDateTime validTo,
            BigDecimal minChargingRate, BigDecimal maxDischargePower, BigDecimal minDischargePower) {
        log.info("충전 프로파일 수정 요청: chargingProfileId={}", chargingProfileId);

        ChargingProfile profile = chargingProfilePort.findByChargingProfileId(chargingProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("ChargingProfile", "chargingProfileId", chargingProfileId));

        ChargingProfile updated = ChargingProfile.builder()
                .id(profile.getId())
                .chargingProfileId(profile.getChargingProfileId())
                .stationId(profile.getStationId())
                .evseId(profile.getEvseId())
                .transactionId(profile.getTransactionId())
                .stackLevel(profile.getStackLevel())
                .chargingProfilePurpose(profile.getChargingProfilePurpose())
                .chargingProfileKind(profile.getChargingProfileKind())
                .validFrom(validFrom != null ? validFrom : profile.getValidFrom())
                .validTo(validTo != null ? validTo : profile.getValidTo())
                .duration(profile.getDuration())
                .startSchedule(profile.getStartSchedule())
                .chargingRateUnit(profile.getChargingRateUnit())
                .minChargingRate(minChargingRate != null ? minChargingRate : profile.getMinChargingRate())
                .isActive(profile.getIsActive())
                .maxDischargePower(maxDischargePower != null ? maxDischargePower : profile.getMaxDischargePower())
                .minDischargePower(minDischargePower != null ? minDischargePower : profile.getMinDischargePower())
                .dischargeRateUnit(profile.getDischargeRateUnit())
                .build();

        return chargingProfilePort.save(updated);
    }

    @Override
    @Transactional
    public void deactivateProfile(Integer chargingProfileId) {
        log.info("충전 프로파일 비활성화 요청: chargingProfileId={}", chargingProfileId);

        ChargingProfile profile = chargingProfilePort.findByChargingProfileId(chargingProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("ChargingProfile", "chargingProfileId", chargingProfileId));

        profile.deactivate();
        chargingProfilePort.save(profile);
        log.info("충전 프로파일 비활성화 완료: chargingProfileId={}", chargingProfileId);
    }
}
