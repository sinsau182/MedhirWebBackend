package com.medhir.rest.settings.leaveSettings.publicHolidays;

import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.service.CompanyService;
import com.medhir.rest.utils.GeneratedId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublicHolidayService {

    private final PublicHolidayRepository publicHolidayRepository;

    @Autowired
    private GeneratedId generatedId;

    @Autowired
    private CompanyService companyService;

    @Autowired
    public PublicHolidayService(PublicHolidayRepository publicHolidayRepository) {
        this.publicHolidayRepository = publicHolidayRepository;
    }

    public PublicHolidayModel createPublicHoliday(PublicHolidayModel holiday) {
        // Check if company exists
        companyService.getCompanyById(holiday.getCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + holiday.getCompanyId()));

        if (publicHolidayRepository.existsByHolidayName(holiday.getHolidayName())) {
            throw new IllegalArgumentException("Holiday with this name already exists");
        }
        if (publicHolidayRepository.existsByDate(holiday.getDate())) {
            throw new IllegalArgumentException("A holiday already exists on this date");
        }

        // Generate new public holiday ID
        String newHolidayId = generatedId.generateId("PH", PublicHolidayModel.class, "holidayId");
        holiday.setHolidayId(newHolidayId);

        return publicHolidayRepository.save(holiday);
    }

    public List<PublicHolidayModel> getAllPublicHolidays() {
        return publicHolidayRepository.findAll();
    }

    public List<PublicHolidayModel> getPublicHolidaysByCompanyId(String companyId) {
        return publicHolidayRepository.findByCompanyId(companyId);
    }

    public PublicHolidayModel getPublicHolidayById(String id) {
        // First try to find by holidayId
        PublicHolidayModel holiday = publicHolidayRepository.findByHolidayId(id)
                .orElse(null);

        // If not found by holidayId, try by MongoDB id
        if (holiday == null) {
            holiday = publicHolidayRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Public holiday not found with id: " + id));
        }

        return holiday;
    }

    public PublicHolidayModel updatePublicHoliday(String id, PublicHolidayModel holiday) {
        PublicHolidayModel existingHoliday = getPublicHolidayById(id);
        
        // Check if company exists if companyId is being updated
        if (holiday.getCompanyId() != null && !holiday.getCompanyId().equals(existingHoliday.getCompanyId())) {
            companyService.getCompanyById(holiday.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + holiday.getCompanyId()));
        }
        
        if (!existingHoliday.getHolidayName().equals(holiday.getHolidayName()) && 
            publicHolidayRepository.existsByHolidayName(holiday.getHolidayName())) {
            throw new IllegalArgumentException("Holiday with name " + holiday.getHolidayName() + " already exists");
        }

        if (!existingHoliday.getDate().equals(holiday.getDate()) && 
            publicHolidayRepository.existsByDate(holiday.getDate())) {
            throw new IllegalArgumentException("A holiday already exists on date: " + holiday.getDate());
        }

        existingHoliday.setHolidayName(holiday.getHolidayName());
        existingHoliday.setDate(holiday.getDate());
        existingHoliday.setDescription(holiday.getDescription());
        
        // Update companyId if provided
        if (holiday.getCompanyId() != null) {
            existingHoliday.setCompanyId(holiday.getCompanyId());
        }

        return publicHolidayRepository.save(existingHoliday);
    }

    public void deletePublicHoliday(String id) {
        PublicHolidayModel holiday = getPublicHolidayById(id);
        publicHolidayRepository.deleteById(holiday.getId());
    }

    public boolean existsByHolidayName(String holidayName) {
        return publicHolidayRepository.existsByHolidayName(holidayName);
    }
}