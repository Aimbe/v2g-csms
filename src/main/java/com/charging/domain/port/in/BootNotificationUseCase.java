package com.charging.domain.port.in;

import com.charging.domain.entity.Station;
import java.util.Optional;

public interface BootNotificationUseCase {
    Optional<Station> processBootNotification(String stationId, String model, String vendorName, String reason);
}
