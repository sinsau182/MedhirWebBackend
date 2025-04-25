package com.medhir.rest.updates;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/updates")
public class UpdatesController {
    private final UpdatesService updatesService;
    @GetMapping("/{employeeId}")
    public ResponseEntity<List<UpdatesModel>> getNotification(@PathVariable String employeeId){
        List<UpdatesModel> notifications = updatesService.getNotifications(employeeId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/register")
    public ResponseEntity<UpdatesModel> registerNotification(@RequestBody UpdateRequest updateRequest){
        UpdatesModel savedNotification = updatesService.registerNotification(updateRequest.getEmployeeId(),updateRequest.getMessage(),updateRequest.getFlag());
        return ResponseEntity.ok(savedNotification);
    }
}
