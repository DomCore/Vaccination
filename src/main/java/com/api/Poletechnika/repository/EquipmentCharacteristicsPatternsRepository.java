package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.EquipmentCharacteristicPattern;
import com.api.Poletechnika.models.MachineryCharacteristicPattern;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentCharacteristicsPatternsRepository extends CrudRepository<EquipmentCharacteristicPattern, Integer> {
    EquipmentCharacteristicPattern findById(int id);
}