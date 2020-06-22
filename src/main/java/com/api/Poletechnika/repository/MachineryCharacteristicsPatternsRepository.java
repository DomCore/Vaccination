package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.MachineryCharacteristicPattern;
import com.api.Poletechnika.models.MachineryCharacteristicSimple;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineryCharacteristicsPatternsRepository extends CrudRepository<MachineryCharacteristicPattern, Integer> {
    MachineryCharacteristicPattern findById(int id);
}