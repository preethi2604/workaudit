package com.workaudit.workaudit.manager.service;

import com.workaudit.workaudit.manager.dto.ManagerRequest;
import com.workaudit.workaudit.manager.dto.ManagerResponse;

import java.util.List;

public interface ManagerService {

    ManagerResponse createManager(ManagerRequest request);

    List<ManagerResponse> getAllManagers();

    ManagerResponse getManagerById(Long id);

    ManagerResponse updateManager(Long id, ManagerRequest request);

    void deleteManager(Long id);
}