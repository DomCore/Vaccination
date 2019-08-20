package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.License;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseRepository extends CrudRepository<License, Integer> {
}