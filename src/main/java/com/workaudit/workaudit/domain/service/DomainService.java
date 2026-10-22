package com.workaudit.workaudit.domain.service;

import com.workaudit.workaudit.domain.dto.DomainRequest;
import com.workaudit.workaudit.domain.dto.DomainResponse;

import java.util.List;

public interface DomainService {

    DomainResponse createDomain(DomainRequest request);

    List<DomainResponse> getAllDomains();

    DomainResponse getDomainById(Long id);

    DomainResponse updateDomain(Long id, DomainRequest request);

    void deleteDomain(Long id);
}