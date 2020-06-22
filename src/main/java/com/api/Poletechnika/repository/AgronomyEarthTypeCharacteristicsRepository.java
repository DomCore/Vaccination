package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.AgronomyEarthTypeCharacteristic;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AgronomyEarthTypeCharacteristicsRepository extends CrudRepository<AgronomyEarthTypeCharacteristic, Integer> {

    AgronomyEarthTypeCharacteristic findById(int id);

    List<AgronomyEarthTypeCharacteristic> findAllByEarthTypeId(int idEatrh);

    @Transactional
    void deleteAllByEarthTypeId(int idEatrh);
}