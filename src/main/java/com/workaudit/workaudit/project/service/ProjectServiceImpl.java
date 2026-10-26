package com.workaudit.workaudit.project.service;

import com.workaudit.workaudit.common.BadRequestException;
import com.workaudit.workaudit.common.ResourceNotFoundException;

import com.workaudit.workaudit.domain.entity.Domain;
import com.workaudit.workaudit.domain.repository.DomainRepository;

import com.workaudit.workaudit.manager.entity.Manager;
import com.workaudit.workaudit.manager.repository.ManagerRepository;

import com.workaudit.workaudit.project.dto.ProjectRequest;
import com.workaudit.workaudit.project.dto.ProjectResponse;
import com.workaudit.workaudit.project.entity.Project;
import com.workaudit.workaudit.project.repository.ProjectRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final DomainRepository domainRepository;
    private final ManagerRepository managerRepository;

    public ProjectServiceImpl(
            ProjectRepository projectRepository,
            DomainRepository domainRepository,
            ManagerRepository managerRepository) {

        this.projectRepository = projectRepository;
        this.domainRepository = domainRepository;
        this.managerRepository = managerRepository;
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request) {

        String name = request.getName().trim();

        if (projectRepository.existsByNameIgnoreCase(name)) {
            throw new BadRequestException(
                    "A project with this name already exists"
            );
        }

        Domain domain = domainRepository.findById(
                request.getDomainId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Domain not found with ID: "
                                + request.getDomainId()
                )
        );

        Manager manager = managerRepository.findById(
                request.getManagerId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Manager not found with ID: "
                                + request.getManagerId()
                )
        );

        Project project = new Project();

        project.setName(name);
        project.setDescription(request.getDescription());
        project.setDomain(domain);
        project.setManager(manager);

        Project savedProject = projectRepository.save(project);

        return mapToResponse(savedProject);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {

        return projectRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {

        Project project = findProjectById(id);

        return mapToResponse(project);
    }

    @Override
    public ProjectResponse updateProject(
            Long id,
            ProjectRequest request) {

        Project project = findProjectById(id);

        String name = request.getName().trim();

        if (projectRepository.existsByNameIgnoreCaseAndIdNot(
                name, id)) {

            throw new BadRequestException(
                    "Another project already uses this name"
            );
        }

        Domain domain = domainRepository.findById(
                request.getDomainId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Domain not found with ID: "
                                + request.getDomainId()
                )
        );

        Manager manager = managerRepository.findById(
                request.getManagerId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Manager not found with ID: "
                                + request.getManagerId()
                )
        );

        project.setName(name);
        project.setDescription(request.getDescription());
        project.setDomain(domain);
        project.setManager(manager);

        Project updatedProject =
                projectRepository.save(project);

        return mapToResponse(updatedProject);
    }

    @Override
    public void deleteProject(Long id) {

        Project project = findProjectById(id);

        try {
            projectRepository.delete(project);
            projectRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException(
                    "Cannot delete this project because other records depend on it"
            );
        }
    }

    private Project findProjectById(Long id) {

        if (id == null || id <= 0) {
            throw new BadRequestException(
                    "Project ID must be a positive number"
            );
        }

        return projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with ID: " + id
                        )
                );
    }

    private ProjectResponse mapToResponse(Project project) {

        ProjectResponse response = new ProjectResponse();

        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());

        response.setDomainId(project.getDomain().getId());
        response.setDomainName(project.getDomain().getName());

        response.setManagerId(project.getManager().getId());
        response.setManagerName(project.getManager().getName());

        response.setCreatedAt(project.getCreatedAt());
        response.setUpdatedAt(project.getUpdatedAt());

        return response;
    }
}