package io.ugurh.oauth2.glee.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import io.ugurh.oauth2.glee.models.Glee;
import io.ugurh.oauth2.user.models.User;

public interface GleeRepository extends PagingAndSortingRepository<Glee, Long>, GleeRepositoryCustom {
    Page<Glee> findAllByUser(User user, Pageable pageable);
}