package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.AgronomyCondition;
import com.api.Poletechnika.models.Policy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgronomyConditionRepository extends CrudRepository<AgronomyCondition, Integer> {

}