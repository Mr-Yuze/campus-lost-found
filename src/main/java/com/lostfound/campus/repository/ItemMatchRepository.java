package com.lostfound.campus.repository;

import com.lostfound.campus.entity.ItemMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemMatchRepository extends JpaRepository<ItemMatch, Long> {
    List<ItemMatch> findByLostReportId(Long lostReportId);
    List<ItemMatch> findByFoundItemId(Long foundItemId);
}
