package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.AgronomyWeather;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AgronomyWeatherRepository extends CrudRepository<AgronomyWeather, Integer> {
    AgronomyWeather findById(String id);

    @Transactional
    void deleteById(String id);
}