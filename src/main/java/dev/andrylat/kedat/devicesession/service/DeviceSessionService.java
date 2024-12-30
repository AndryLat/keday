package dev.andrylat.kedat.devicesession.service;

import dev.andrylat.kedat.common.model.device.session.DeviceSession;
import java.util.concurrent.CompletableFuture;
import org.springframework.kafka.support.SendResult;

public interface DeviceSessionService {
  CompletableFuture<SendResult<String, DeviceSession>> sendSession(DeviceSession deviceSession);
}
