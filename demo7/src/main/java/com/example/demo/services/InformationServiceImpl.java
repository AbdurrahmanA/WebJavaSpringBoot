package com.example.demo.services;

import com.example.demo.data.InformationEntity;
import com.example.demo.repositories.InformationEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InformationServiceImpl implements InformationService {

    @Autowired
    private InformationEntityRepository informationRepository;

    @Override
    public InformationEntity createInformation(InformationEntity info) {
        return informationRepository.save(info);
    }

    @Override
    public List<InformationEntity> getAllInformation() {
        return informationRepository.findAll();
    }

    @Override
    public Optional<InformationEntity> getInformationById(Integer id) {
        return informationRepository.findById(id);
    }

    @Override
    public List<InformationEntity> getInformationByOwner(Integer ownerId) {
        return informationRepository.findAllByOwnerId(ownerId);
    }

    @Override
    public void deleteInformation(Integer id) {
        informationRepository.deleteById(id);
    }

    // Filtrelenmiş sonuçlar - herkesin bilgisi
    @Override
    public List<InformationEntity> getAllInformationFiltered(Integer categoryId, String startDate, String endDate) {
        return filter(informationRepository.findAll(), categoryId, startDate, endDate);
    }

    // Filtrelenmiş sonuçlar - sadece sahibine göre
    @Override
    public List<InformationEntity> getInformationByOwnerFiltered(Integer ownerId, Integer categoryId, String startDate, String endDate) {
        return filter(informationRepository.findAllByOwnerId(ownerId), categoryId, startDate, endDate);
    }

    private List<InformationEntity> filter(List<InformationEntity> list, Integer categoryId, String start, String end) {
        return list.stream()
                .filter(i -> categoryId == null || i.getCategory().getId().equals(categoryId))
                .filter(i -> {
                    if (start == null || start.isEmpty()) return true;
                    LocalDate startDate = LocalDate.parse(start);
                    return !i.getDateAdded().isBefore(startDate);
                })
                .filter(i -> {
                    if (end == null || end.isEmpty()) return true;
                    LocalDate endDate = LocalDate.parse(end);
                    return !i.getDateAdded().isAfter(endDate);
                })
                .collect(Collectors.toList());
    }
}
