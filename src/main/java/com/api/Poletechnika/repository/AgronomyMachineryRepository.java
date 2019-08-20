package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.AgronomyCondition;
import com.api.Poletechnika.models.AgronomyMachinery;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgronomyMachineryRepository extends CrudRepository<AgronomyMachinery, Integer> {
    AgronomyMachinery findById(String typeId);

}