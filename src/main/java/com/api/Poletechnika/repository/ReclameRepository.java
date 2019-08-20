package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.Reclam;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReclameRepository extends CrudRepository<Reclam, Integer> {

    //List<Reclam> findReclamByType(String type);
    Reclam findReclamByType(String type);
}