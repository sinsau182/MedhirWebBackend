package com.medhir.rest.employee;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends MongoRepository<EmployeeModel, String> {
    Optional<EmployeeModel> findByEmailPersonal(String email);

    Optional<EmployeeModel> findByPhone(String phone);

    Optional<EmployeeModel> findByEmployeeId(String employeeId);

    List<EmployeeModel> findByUpdateStatus(String updateStatus);

    List<EmployeeModel> findByReportingManager(String reportingManager);

    Optional<EmployeeModel> findByCompanyIdAndEmployeeId(String companyId, String employeeId);

    List<EmployeeModel> findByCompanyId(String companyId);

    List<EmployeeModel> findByCompanyIdAndReportingManager(String companyId, String reportingManager);

    List<EmployeeModel> findByDepartmentAndDesignationIn(String department, List<String> designations);
}
