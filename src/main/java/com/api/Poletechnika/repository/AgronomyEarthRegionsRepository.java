package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.AgronomyEarthRegion;
import com.api.Poletechnika.models.AgronomyEarthType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AgronomyEarthRegionsRepository extends CrudRepository<AgronomyEarthRegion, Integer> {

    AgronomyEarthRegion findById(int id);

    @Transactional
    void deleteById(int id);
}