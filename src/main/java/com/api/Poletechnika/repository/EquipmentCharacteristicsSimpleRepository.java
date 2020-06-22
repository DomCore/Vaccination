package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.EquipmentCharacteristicSimple;
import com.api.Poletechnika.models.MachineryCharacteristicSimple;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EquipmentCharacteristicsSimpleRepository extends CrudRepository<EquipmentCharacteristicSimple, Integer> {
    List<EquipmentCharacteristicSimple> findAllByIdEquipment(int id);

    EquipmentCharacteristicSimple findById(int id);

    @Transactional
    void deleteAllByIdEquipment(int id);
}