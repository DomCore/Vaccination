package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.EquipmentCharacteristicGeneral;
import com.api.Poletechnika.models.MachineryCharacteristicGeneral;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EquiomentCharacteristicsGeneralRepository extends CrudRepository<EquipmentCharacteristicGeneral, Integer> {
    List<EquipmentCharacteristicGeneral> findAllByIdEquipment(int id);

    @Transactional
    void deleteAllByIdEquipment(int id);
}