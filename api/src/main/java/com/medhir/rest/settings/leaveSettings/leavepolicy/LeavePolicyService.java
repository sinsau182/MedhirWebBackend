package com.medhir.rest.settings.leaveSettings.leavepolicy;

import com.medhir.rest.exception.BadRequestException;
import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.settings.leaveSettings.leaveType.LeaveTypeService;
import com.medhir.rest.utils.GeneratedId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeavePolicyService {

    @Autowired
    private LeavePolicyRepository leavePolicyRepository;

    @Autowired
    private LeaveTypeService leaveTypeService;

    @Autowired
    private GeneratedId generatedId;

    public LeavePolicyModel createLeavePolicy(LeavePolicyModel leavePolicy) {
        if (leavePolicyRepository.existsByName(leavePolicy.getName())) {
            throw new DuplicateResourceException("Leave policy with name " + leavePolicy.getName() + " already exists");
        }

        validateLeaveAllocations(leavePolicy.getLeaveAllocations());
        
        // Generate new leave policy ID
        String newLeavePolicyId = generatedId.generateId("LP", LeavePolicyModel.class, "leavePolicyId");
        leavePolicy.setLeavePolicyId(newLeavePolicyId);

        leavePolicy.setCreatedAt(LocalDateTime.now().toString());
        leavePolicy.setUpdatedAt(LocalDateTime.now().toString());
        return leavePolicyRepository.save(leavePolicy);
    }

    public List<LeavePolicyModel> getAllLeavePolicies() {
        return leavePolicyRepository.findAll();
    }

    public LeavePolicyModel getLeavePolicyById(String id) {
        // First try to find by leavePolicyId
        LeavePolicyModel leavePolicy = leavePolicyRepository.findByLeavePolicyId(id)
                .orElse(null);

        // If not found by leavePolicyId, try by MongoDB id
        if (leavePolicy == null) {
            leavePolicy = leavePolicyRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Leave policy not found with id: " + id));
        }

        return leavePolicy;
    }

    public LeavePolicyModel updateLeavePolicy(String id, LeavePolicyModel leavePolicy) {
        LeavePolicyModel existingPolicy = getLeavePolicyById(id);
        
        if (!existingPolicy.getName().equals(leavePolicy.getName()) && 
            leavePolicyRepository.existsByName(leavePolicy.getName())) {
            throw new DuplicateResourceException("Leave policy with name " + leavePolicy.getName() + " already exists");
        }

        validateLeaveAllocations(leavePolicy.getLeaveAllocations());

        existingPolicy.setName(leavePolicy.getName());
        existingPolicy.setLeaveAllocations(leavePolicy.getLeaveAllocations());
        existingPolicy.setUpdatedAt(LocalDateTime.now().toString());

        return leavePolicyRepository.save(existingPolicy);
    }

    public void deleteLeavePolicy(String id) {
        LeavePolicyModel leavePolicy = getLeavePolicyById(id);
        leavePolicyRepository.deleteById(leavePolicy.getId());
    }

    private void validateLeaveAllocations(List<LeaveAllocation> allocations) {
        if (allocations == null || allocations.isEmpty()) {
            throw new BadRequestException("Leave allocations cannot be empty");
        }

        for (LeaveAllocation allocation : allocations) {
            // Verify leave type exists
            leaveTypeService.getLeaveTypeById(allocation.getLeaveTypeId());
        }
    }
} 