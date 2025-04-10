package com.medhir.rest.settings.department;

import com.medhir.rest.exception.BadRequestException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.settings.leaveSettings.leavepolicy.LeavePolicyService;
import com.medhir.rest.utils.GeneratedId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private LeavePolicyService leavePolicyService;

    @Autowired
    private GeneratedId generatedId;

    public DepartmentModel createDepartment(DepartmentModel department) {
        if (departmentRepository.existsByName(department.getName())) {
            throw new BadRequestException("Department with name " + department.getName() + " already exists");
        }

        // Verify leave policy exists
        leavePolicyService.getLeavePolicyById(department.getLeavePolicy());

        // Generate new department ID using the utility class
        String newDepartmentId = generatedId.generateId("DEPT", DepartmentModel.class, "departmentId");
        department.setDepartmentId(newDepartmentId);

        department.setCreatedAt(LocalDateTime.now().toString());
        department.setUpdatedAt(LocalDateTime.now().toString());
        return departmentRepository.save(department);
    }

    public List<DepartmentModel> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public DepartmentModel getDepartmentById(String id) {
        // First try to find by departmentId
        DepartmentModel department = departmentRepository.findByDepartmentId(id)
                .orElse(null);

        // If not found by departmentId, try by MongoDB id
        if (department == null) {
            department = departmentRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        }

        return department;
    }

    public DepartmentModel updateDepartment(String id, DepartmentModel department) {
        DepartmentModel existingDepartment = getDepartmentById(id);
        
        if (!existingDepartment.getName().equals(department.getName()) && 
            departmentRepository.existsByName(department.getName())) {
            throw new BadRequestException("Department with name " + department.getName() + " already exists");
        }

        // Verify leave policy exists
        leavePolicyService.getLeavePolicyById(department.getLeavePolicy());

        existingDepartment.setName(department.getName());
        existingDepartment.setDescription(department.getDescription());
        existingDepartment.setDepartmentHead(department.getDepartmentHead());
        existingDepartment.setLeavePolicy(department.getLeavePolicy());
        existingDepartment.setWeeklyHolidays(department.getWeeklyHolidays());
        existingDepartment.setUpdatedAt(LocalDateTime.now().toString());

        return departmentRepository.save(existingDepartment);
    }

    public void deleteDepartment(String id) {
        DepartmentModel department = getDepartmentById(id);
        departmentRepository.deleteById(department.getId());
    }
}
