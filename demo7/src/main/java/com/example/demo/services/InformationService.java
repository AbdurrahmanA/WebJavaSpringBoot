package com.example.demo.services;

import com.example.demo.data.InformationEntity;

import java.util.List;
import java.util.Optional;

public interface InformationService {
    InformationEntity createInformation(InformationEntity info);
    List<InformationEntity> getAllInformation();
    Optional<InformationEntity> getInformationById(Integer id);
    List<InformationEntity> getInformationByOwner(Integer ownerId);
    void deleteInformation(Integer id);

        List<InformationEntity> getAllInformationFiltered(Integer categoryId, String startDate, String endDate);
    List<InformationEntity> getInformationByOwnerFiltered(Integer ownerId, Integer categoryId, String startDate, String endDate);
}
