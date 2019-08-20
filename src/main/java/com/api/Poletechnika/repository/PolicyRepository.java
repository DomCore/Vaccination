package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.Policy;
import com.api.Poletechnika.models.Reclam;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepository extends CrudRepository<Policy, Integer> {

    //List<Reclam> findReclamByType();
    Policy findById(int id);
}