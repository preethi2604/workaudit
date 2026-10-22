package com.workaudit.workaudit.manager.service;

import com.workaudit.workaudit.common.BadRequestException;
import com.workaudit.workaudit.common.ResourceNotFoundException;
import com.workaudit.workaudit.manager.dto.ManagerRequest;
import com.workaudit.workaudit.manager.dto.ManagerResponse;
import com.workaudit.workaudit.manager.entity.Manager;
import com.workaudit.workaudit.manager.repository.ManagerRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ManagerServiceImpl implements ManagerService {

    private final ManagerRepository managerRepository;

    public ManagerServiceImpl(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @Override
    public ManagerResponse createManager(ManagerRequest request) {

        String name = request.getName().trim();

        if (managerRepository.existsByNameIgnoreCase(name)) {
            throw new BadRequestException(
                    "Manager with this name already exists"
            );
        }

        Manager manager = new Manager();
        manager.setName(name);

        Manager savedManager = managerRepository.save(manager);

        return mapToResponse(savedManager);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManagerResponse> getAllManagers() {

        return managerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ManagerResponse getManagerById(Long id) {

        Manager manager = findManagerById(id);

        return mapToResponse(manager);
    }

    @Override
    public ManagerResponse updateManager(
            Long id,
            ManagerRequest request) {

        Manager manager = findManagerById(id);

        String name = request.getName().trim();

        if (managerRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new BadRequestException(
                    "Another manager with this name already exists"
            );
        }

        manager.setName(name);

        Manager updatedManager = managerRepository.save(manager);

        return mapToResponse(updatedManager);
    }

    @Override
    public void deleteManager(Long id) {

        Manager manager = findManagerById(id);

        try {
            managerRepository.delete(manager);
            managerRepository.flush();

        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException(
                    "Cannot delete this manager because it is referenced by a project"
            );
        }
    }

    private Manager findManagerById(Long id) {

        return managerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Manager not found with ID: " + id
                        )
                );
    }

    private ManagerResponse mapToResponse(Manager manager) {

        return new ManagerResponse(
                manager.getId(),
                manager.getName()
        );
    }
}