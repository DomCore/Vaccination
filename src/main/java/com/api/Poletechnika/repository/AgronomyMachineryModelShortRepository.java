package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.AgronomyMachineryModel;
import com.api.Poletechnika.models.AgronomyMachineryModelShort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgronomyMachineryModelShortRepository extends CrudRepository<AgronomyMachineryModelShort, Integer> {

    List<AgronomyMachineryModelShort> findAllByTypeId(String typeId);
}