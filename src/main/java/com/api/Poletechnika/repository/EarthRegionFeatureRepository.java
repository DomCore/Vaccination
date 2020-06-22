package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.AgronomyEarthType;
import com.api.Poletechnika.models.EarthRegionFeature;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EarthRegionFeatureRepository extends CrudRepository<EarthRegionFeature, Integer> {

    EarthRegionFeature findById(int id);

    EarthRegionFeature findByIdAndRegionId(int id, int regionId);

    List<EarthRegionFeature> findAllByRegionId(int id);

    @Transactional
    void deleteById(int id);
}