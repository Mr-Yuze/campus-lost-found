package com.lostfound.campus.repository;

import com.lostfound.campus.entity.FoundItem;
import com.lostfound.campus.entity.FoundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoundItemRepository extends JpaRepository<FoundItem, Long> {
    List<FoundItem> findByCategory(String category);
    List<FoundItem> findByStatus(FoundStatus status);
    List<FoundItem> findByReportedById(Long userId);

    @org.springframework.data.jpa.repository.Query("SELECT f FROM FoundItem f WHERE " +
           "(:category IS NULL OR f.category = :category) AND " +
           "(:status IS NULL OR f.status = :status) AND " +
           "(:location IS NULL OR f.locationFound LIKE %:location%) AND " +
           "(:keyword IS NULL OR f.itemName LIKE %:keyword% OR f.description LIKE %:keyword%)")
    List<FoundItem> searchFoundItems(@org.springframework.data.repository.query.Param("category") String category, 
                                     @org.springframework.data.repository.query.Param("status") FoundStatus status, 
                                     @org.springframework.data.repository.query.Param("location") String location, 
                                     @org.springframework.data.repository.query.Param("keyword") String keyword);
}
