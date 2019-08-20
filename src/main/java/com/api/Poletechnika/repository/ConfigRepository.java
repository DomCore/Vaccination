package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.ConfigModel;
import com.api.Poletechnika.models.License;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends CrudRepository<ConfigModel, Integer> {

    ConfigModel findByName(String name);
}