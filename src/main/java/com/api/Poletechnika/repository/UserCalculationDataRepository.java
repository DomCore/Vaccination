package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.UserCalculation;
import com.api.Poletechnika.models.UserCalculationData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserCalculationDataRepository extends CrudRepository<UserCalculationData, Integer> {

   List<UserCalculationData> findAllByIdCalculationAndType(int idCalculation, String type);

   @Transactional
   void deleteAllByIdCalculation(int id_calculation);
}