package com.medhir.rest.settings.designations;

import com.medhir.rest.dto.CompanyDesignationDTO;
import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.settings.department.DepartmentModel;
import com.medhir.rest.settings.department.DepartmentService;
import com.medhir.rest.utils.GeneratedId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DesignationService {

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GeneratedId generatedId;

    @Autowired
    private DepartmentService departmentService;

    public DesignationModel createDesignation(DesignationModel designation) {
        if (designationRepository.existsByNameAndDepartment(designation.getName(), designation.getDepartment())) {
            throw new DuplicateResourceException("Designation with name " + designation.getName() + " already exists in this department");
        }

        // Verify leave policy exists
        departmentService.getDepartmentById(designation.getDepartment());

        // Generate new designation ID using the utility class
        String newDesignationId = generatedId.generateId("DES", DesignationModel.class, "designationId");
        designation.setDesignationId(newDesignationId);

        designation.setCreatedAt(LocalDateTime.now().toString());
        designation.setUpdatedAt(LocalDateTime.now().toString());
        return designationRepository.save(designation);
    }

    public List<DesignationModel> getAllDesignations() {
        return designationRepository.findAll();
    }

    public List<DesignationModel> getDesignationsByDepartment(String departmentId) {
        return designationRepository.findByDepartment(departmentId);
    }

    public DesignationModel getDesignationById(String id) {
        // First try to find by designationId
        DesignationModel designation = designationRepository.findByDesignationId(id)
                .orElse(null);

        // If not found by designationId, try by MongoDB id
        if (designation == null) {
            designation = designationRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Designation not found with id: " + id));
        }

        return designation;
    }

    public DesignationModel updateDesignation(String id, DesignationModel designation) {
        DesignationModel existingDesignation = getDesignationById(id);

        // Check name uniqueness only if name is being updated
        if (designation.getName() != null && !designation.getName().isEmpty() &&
                !existingDesignation.getName().equals(designation.getName()) &&
                designationRepository.existsByNameAndDepartment(designation.getName(), designation.getDepartment())) {
            throw new DuplicateResourceException("Designation with name " + designation.getName() + " already exists in this department");
        }

        // Update only the fields that are provided in the request
        if (designation.getName() != null && !designation.getName().isEmpty()) {
            existingDesignation.setName(designation.getName());
        }

        if (designation.getDescription() != null) {
            existingDesignation.setDescription(designation.getDescription());
        }

        if (designation.getDepartment() != null && !designation.getDepartment().isEmpty()) {
            existingDesignation.setDepartment(designation.getDepartment());
        }

        existingDesignation.setManager(designation.isManager());
        existingDesignation.setUpdatedAt(LocalDateTime.now().toString());
        existingDesignation.setOvertimeEligible(designation.isOvertimeEligible());

        return designationRepository.save(existingDesignation);
    }


    public void deleteDesignation(String id) {
        DesignationModel designation = getDesignationById(id);
        designationRepository.delete(designation);
    }

    public List<CompanyDesignationDTO> getAllDesignationsByCompanyId(String companyId) {
        // Get all departments for the company
        List<DepartmentModel> departments = departmentService.getDepartmentsByCompanyId(companyId);
        
        // Get all designations for each department
        return departments.stream()
            .flatMap(department -> {
                List<DesignationModel> designations = designationRepository.findByDepartment(department.getDepartmentId());
                return designations.stream()
                    .map(designation -> new CompanyDesignationDTO(
                        designation.getDesignationId(),
                        designation.getName(),
                        department.getName(), // Department name instead of ID
                        designation.getDescription(),
                        designation.isManager(),
                        designation.isOvertimeEligible()
                    ));
            })
            .collect(Collectors.toList());
    }
}