package com.workaudit.workaudit.repository;

import com.workaudit.workaudit.entity.Domain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DomainRepository
        extends JpaRepository<Domain, UUID> {

    Optional<Domain> findByCode(String code);

    Optional<Domain> findByName(String name);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    List<Domain> findByActiveTrueOrderByNameAsc();

    long countByActiveTrue();
}