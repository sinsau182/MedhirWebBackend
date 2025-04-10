package com.medhir.rest.settings.leaveSettings.leaveType;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaveTypeRepository extends MongoRepository<LeaveTypeModel, String> {
    Optional<LeaveTypeModel> findByLeaveTypeName(String leaveTypeName);
    boolean existsByLeaveTypeName(String leaveTypeName);
    Optional<LeaveTypeModel> findByLeaveTypeId(String leaveTypeId);
    boolean existsByLeaveTypeId(String leaveTypeId);
}