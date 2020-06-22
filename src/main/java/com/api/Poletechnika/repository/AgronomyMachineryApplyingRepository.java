package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.AgronomyMachineryApplying;
import com.api.Poletechnika.models.AgronomyMachineryModelShort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgronomyMachineryApplyingRepository extends CrudRepository<AgronomyMachineryApplying, Integer> {

    AgronomyMachineryApplying findById(int id);

    AgronomyMachineryApplying findByName(String name);
}