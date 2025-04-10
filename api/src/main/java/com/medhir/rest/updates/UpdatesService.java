package com.medhir.rest.updates;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdatesService {
    private final UpdatesRepository updatesRepository;

    public List<UpdatesModel> getNotifications(String employeeId) {
        return updatesRepository.findByEmployeeIdOrderByTimestampDesc(employeeId);
    }

    public UpdatesModel registerNotification(String employeeId, String message, String flag) {
        UpdatesModel update = new UpdatesModel();
        update.setEmployeeId(employeeId);
        update.setMessage(message);
        update.setFlag(flag);
        update.setTimestamp(LocalDateTime.now()); // Automatically converted to UTC

        return updatesRepository.save(update);
    }
}
