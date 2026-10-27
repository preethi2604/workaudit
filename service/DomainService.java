package com.workaudit.workaudit.service;

import com.workaudit.workaudit.dto.DomainRequest;
import com.workaudit.workaudit.dto.DomainResponse;
import com.workaudit.workaudit.entity.Domain;
import com.workaudit.workaudit.repository.DomainRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class DomainService {

    private final DomainRepository domainRepository;

    public DomainService(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    public DomainResponse createDomain(DomainRequest request) {

        String name = request.getName().trim();
        String code = request.getCode().trim().toUpperCase(Locale.ROOT);

        if (domainRepository.existsByName(name)) {
            throw new IllegalArgumentException(
                    "Domain with name '" + name + "' already exists"
            );
        }

        if (domainRepository.existsByCode(code)) {
            throw new IllegalArgumentException(
                    "Domain with code '" + code + "' already exists"
            );
        }

        Domain domain = new Domain(
                name,
                code,
                request.getDescription()
        );

        Domain savedDomain = domainRepository.save(domain);

        return DomainResponse.fromEntity(savedDomain);
    }

    @Transactional(readOnly = true)
    public List<DomainResponse> getActiveDomains() {

        return domainRepository
                .findByActiveTrueOrderByNameAsc()
                .stream()
                .map(DomainResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public DomainResponse getDomain(UUID domainId) {

        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Domain not found: " + domainId
                        )
                );

        return DomainResponse.fromEntity(domain);
    }

    public DomainResponse updateDomain(
            UUID domainId,
            DomainRequest request
    ) {

        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Domain not found: " + domainId
                        )
                );

        String name = request.getName().trim();
        String code = request.getCode().trim().toUpperCase(Locale.ROOT);

        domainRepository.findByName(name)
                .filter(existing -> !existing.getId().equals(domainId))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Domain with name '" + name + "' already exists"
                    );
                });

        domainRepository.findByCode(code)
                .filter(existing -> !existing.getId().equals(domainId))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Domain with code '" + code + "' already exists"
                    );
                });

        domain.setName(name);
        domain.setCode(code);
        domain.setDescription(request.getDescription());

        return DomainResponse.fromEntity(domainRepository.save(domain));
    }

    public void deactivateDomain(UUID domainId) {

        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Domain not found: " + domainId
                        )
                );

        domain.setActive(false);

        domainRepository.save(domain);
    }
}