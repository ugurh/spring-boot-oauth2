package io.ugurh.oauth2.glee.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import io.ugurh.oauth2.glee.models.Glee;

import java.time.LocalDate;
import java.time.LocalTime;

public interface GleeRepositoryCustom {
	
    Page<Glee> filter(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Param("fromDate") LocalDate fromDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Param("toDate") LocalDate toDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            @Param("fromTime") LocalTime fromTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            @Param("toTime") LocalTime toTime,
            @Param("text") String text,
            @Param("value") Double value,
            @Param("userId") Long userId,
            Pageable p);
}
