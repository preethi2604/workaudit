package com.workaudit.workaudit.domain.service;

import com.workaudit.workaudit.common.BadRequestException;
import com.workaudit.workaudit.common.ResourceNotFoundException;
import com.workaudit.workaudit.domain.dto.DomainRequest;
import com.workaudit.workaudit.domain.dto.DomainResponse;
import com.workaudit.workaudit.domain.entity.Domain;
import com.workaudit.workaudit.domain.repository.DomainRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DomainServiceImpl implements DomainService {

    private final DomainRepository domainRepository;

    public DomainServiceImpl(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    @Override
    public DomainResponse createDomain(DomainRequest request) {

        String name = request.getName().trim();

        if (domainRepository.existsByNameIgnoreCase(name)) {
            throw new BadRequestException(
                    "Domain already exists: " + name
            );
        }

        Domain domain = new Domain();
        domain.setName(name);

        Domain savedDomain = domainRepository.save(domain);

        return mapToResponse(savedDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainResponse> getAllDomains() {

        return domainRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DomainResponse getDomainById(Long id) {

        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Domain not found with ID: " + id
                ));

        return mapToResponse(domain);
    }

    @Override
    public DomainResponse updateDomain(
            Long id,
            DomainRequest request) {

        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Domain not found with ID: " + id
                ));

        String name = request.getName().trim();

        if (domainRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new BadRequestException(
                    "Another domain already uses the name: " + name
            );
        }

        domain.setName(name);

        Domain updatedDomain = domainRepository.save(domain);

        return mapToResponse(updatedDomain);
    }

    @Override
    public void deleteDomain(Long id) {

        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Domain not found with ID: " + id
                ));

        try {
            domainRepository.delete(domain);
            domainRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new BadRequestException(
                    "Cannot delete this domain because it is associated "
                            + "with existing projects."
            );
        }
    }

    private DomainResponse mapToResponse(Domain domain) {

        return new DomainResponse(
                domain.getId(),
                domain.getName()
        );
    }
}