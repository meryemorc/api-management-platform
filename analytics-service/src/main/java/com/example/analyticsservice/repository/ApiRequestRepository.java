package com.example.analyticsservice.repository;

import com.example.analyticsservice.document.ApiRequestDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ApiRequestRepository extends MongoRepository<ApiRequestDocument, String> {

    // Organizasyonun belirli tarih aralığındaki istekleri
    List<ApiRequestDocument> findByOrganizationIdAndTimestampBetween(
            String organizationId,
            LocalDateTime start,
            LocalDateTime end
    );

    // Toplam istek sayısı
    long countByOrganizationIdAndTimestampBetween(
            String organizationId,
            LocalDateTime start,
            LocalDateTime end
    );
}