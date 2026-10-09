package com.workaudit.workaudit.report.repository;

import com.workaudit.workaudit.report.entity.ManagerMeeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerMeetingRepository extends JpaRepository<ManagerMeeting, Long> {
    Optional<ManagerMeeting> findByReportId(Long reportId);
}