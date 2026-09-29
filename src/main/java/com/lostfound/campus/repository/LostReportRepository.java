package com.lostfound.campus.repository;

import com.lostfound.campus.entity.LostReport;
import com.lostfound.campus.entity.LostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LostReportRepository extends JpaRepository<LostReport, Long> {
    List<LostReport> findByCategory(String category);
    List<LostReport> findByStatus(LostStatus status);
    List<LostReport> findByReportedById(Long userId);

    @org.springframework.data.jpa.repository.Query("SELECT l FROM LostReport l WHERE " +
           "(:category IS NULL OR l.category = :category) AND " +
           "(:status IS NULL OR l.status = :status) AND " +
           "(:location IS NULL OR l.locationLost LIKE %:location%) AND " +
           "(:keyword IS NULL OR l.itemName LIKE %:keyword% OR l.description LIKE %:keyword%)")
    List<LostReport> searchLostReports(@org.springframework.data.repository.query.Param("category") String category, 
                                       @org.springframework.data.repository.query.Param("status") LostStatus status, 
                                       @org.springframework.data.repository.query.Param("location") String location, 
                                       @org.springframework.data.repository.query.Param("keyword") String keyword);
}
