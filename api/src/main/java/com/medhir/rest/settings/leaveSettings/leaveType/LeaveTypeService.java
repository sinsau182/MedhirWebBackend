package com.medhir.rest.settings.leaveSettings.leaveType;

import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.service.CompanyService;
import com.medhir.rest.utils.GeneratedId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveTypeService {

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private GeneratedId generatedId;

    @Autowired
    private CompanyService companyService;

    public LeaveTypeModel createLeaveType(LeaveTypeModel leaveType) {
        // Check if company exists
        companyService.getCompanyById(leaveType.getCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + leaveType.getCompanyId()));

        if (leaveTypeRepository.existsByLeaveTypeName(leaveType.getLeaveTypeName())) {
            throw new DuplicateResourceException("Leave type with this name " + leaveType.getLeaveTypeName() +" already exists");
        }

        // Generate new leave type ID
        String newLeaveTypeId = generatedId.generateId("LT", LeaveTypeModel.class, "leaveTypeId");
        leaveType.setLeaveTypeId(newLeaveTypeId);

        return leaveTypeRepository.save(leaveType);
    }

    public List<LeaveTypeModel> getAllLeaveTypes() {
        return leaveTypeRepository.findAll();
    }

    public List<LeaveTypeModel> getLeaveTypesByCompanyId(String companyId) {
        return leaveTypeRepository.findByCompanyId(companyId);
    }

    public LeaveTypeModel getLeaveTypeById(String id) {
        // First try to find by leaveTypeId
        LeaveTypeModel leaveType = leaveTypeRepository.findByLeaveTypeId(id)
                .orElse(null);

        // If not found by leaveTypeId, try by MongoDB id
        if (leaveType == null) {
            leaveType = leaveTypeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Leave type not found with id: " + id));
        }

        return leaveType;
    }

    public LeaveTypeModel updateLeaveType(String id, LeaveTypeModel leaveType) {
        LeaveTypeModel existingLeaveType = getLeaveTypeById(id);

        // Check if company exists if companyId is being updated
        if (leaveType.getCompanyId() != null && !leaveType.getCompanyId().equals(existingLeaveType.getCompanyId())) {
            companyService.getCompanyById(leaveType.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + leaveType.getCompanyId()));
        }

        if (!existingLeaveType.getLeaveTypeName().equals(leaveType.getLeaveTypeName()) &&
                leaveTypeRepository.existsByLeaveTypeName(leaveType.getLeaveTypeName())) {
            throw new IllegalArgumentException("Leave type with name " + leaveType.getLeaveTypeName() + " already exists");
        }

        existingLeaveType.setLeaveTypeName(leaveType.getLeaveTypeName());
        existingLeaveType.setDescription(leaveType.getDescription());
        existingLeaveType.setAccrualPeriod(leaveType.getAccrualPeriod());
        existingLeaveType.setAllowedInNoticePeriod(leaveType.isAllowedInNoticePeriod());
        existingLeaveType.setAllowedInProbationPeriod(leaveType.isAllowedInProbationPeriod());
        existingLeaveType.setCanBeCarriedForward(leaveType.isCanBeCarriedForward());
        
        // Update companyId if provided
        if (leaveType.getCompanyId() != null) {
            existingLeaveType.setCompanyId(leaveType.getCompanyId());
        }

        return leaveTypeRepository.save(existingLeaveType);
    }


    public void deleteLeaveType(String id) {
        LeaveTypeModel leaveType = getLeaveTypeById(id);
        leaveTypeRepository.deleteById(leaveType.getId());
    }

    public boolean existsByLeaveTypeName(String leaveTypeName) {
        return leaveTypeRepository.existsByLeaveTypeName(leaveTypeName);
    }

    public boolean existsByName(String name) {
        return leaveTypeRepository.existsByLeaveTypeName(name);
    }
}