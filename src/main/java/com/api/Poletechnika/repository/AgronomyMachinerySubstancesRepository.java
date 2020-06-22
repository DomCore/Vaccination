package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.AgronomyMachineryModel;
import com.api.Poletechnika.models.AgronomyMachinerySubstance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AgronomyMachinerySubstancesRepository extends CrudRepository<AgronomyMachinerySubstance, Integer> {

    AgronomyMachinerySubstance findById(int itemId);
}