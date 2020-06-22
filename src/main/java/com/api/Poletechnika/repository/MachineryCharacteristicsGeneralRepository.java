package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.MachineryCharacteristicGeneral;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MachineryCharacteristicsGeneralRepository extends CrudRepository<MachineryCharacteristicGeneral, Integer> {
    List<MachineryCharacteristicGeneral> findAllByIdMachinery(int idMachinery);

    @Transactional
    void deleteAllByIdMachinery(int idMachinery);
}