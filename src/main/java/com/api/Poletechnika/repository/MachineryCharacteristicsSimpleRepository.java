package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.Breakdown;
import com.api.Poletechnika.models.MachineryCharacteristicGeneral;
import com.api.Poletechnika.models.MachineryCharacteristicSimple;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MachineryCharacteristicsSimpleRepository extends CrudRepository<MachineryCharacteristicSimple, Integer> {
    List<MachineryCharacteristicSimple> findAllByIdMachinery(int idMachinery);

    MachineryCharacteristicSimple findById(int id);

    @Transactional
    void deleteAllByIdMachinery(int idMachinery);
}