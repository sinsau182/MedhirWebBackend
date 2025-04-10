package com.medhir.rest.settings.leaveSettings.publicHolidays;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PublicHolidayRepository extends MongoRepository<PublicHolidayModel, String> {
    Optional<PublicHolidayModel> findByHolidayName(String holidayName);
    boolean existsByHolidayName(String holidayName);
    boolean existsByDate(LocalDate date);
    Optional<PublicHolidayModel> findByHolidayId(String holidayId);
    boolean existsByHolidayId(String holidayId);
}