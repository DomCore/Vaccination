package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.AgronomyEarthCategory;
import com.api.Poletechnika.models.EarthRegionFeature;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgronomyEarthCategoriesRepository extends CrudRepository<AgronomyEarthCategory, Integer> {

    AgronomyEarthCategory findById(int id);

    AgronomyEarthCategory findByName(String name);

}