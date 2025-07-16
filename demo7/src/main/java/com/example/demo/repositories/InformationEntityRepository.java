package com.example.demo.repositories;

import com.example.demo.data.InformationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InformationEntityRepository extends JpaRepository<InformationEntity, Integer> {
    List<InformationEntity> findAllByOwnerId(Integer ownerId);
}