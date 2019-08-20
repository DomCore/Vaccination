package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.UserCalculation;
import com.api.Poletechnika.models.UserGlobalData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserCalculationRepository extends CrudRepository<UserCalculation, Integer> {

    List<UserCalculation> findAllByUserId(int id);

    UserCalculation findByUserIdAndId(int userId, int id);

    @Transactional
    void deleteUserCalculationByUserIdAndId(int userId, int id);
}