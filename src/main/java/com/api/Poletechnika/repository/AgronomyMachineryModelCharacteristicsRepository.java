package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.AgronomyMachineryModelCharacteristic;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AgronomyMachineryModelCharacteristicsRepository extends CrudRepository<AgronomyMachineryModelCharacteristic, Integer> {
    List<AgronomyMachineryModelCharacteristic> findAllByIdMachinery(int idMachinery);

    @Transactional
    void deleteAllByIdMachinery(int idMachinery);
}