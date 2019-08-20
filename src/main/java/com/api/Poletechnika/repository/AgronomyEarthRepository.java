package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.AgronomyEarth;
import com.api.Poletechnika.models.AgronomyMachinery;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgronomyEarthRepository extends CrudRepository<AgronomyEarth, Integer> {

}