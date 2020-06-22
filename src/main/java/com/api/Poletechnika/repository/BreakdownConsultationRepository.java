package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.Breakdown;
import com.api.Poletechnika.models.BreakdownConsultation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BreakdownConsultationRepository extends CrudRepository<BreakdownConsultation, Integer> {

    BreakdownConsultation findById(int id);

    Iterable<BreakdownConsultation> findAllByBreakdownId(int id);


    Iterable<BreakdownConsultation> findAllByBreakdownIdAndIsNew(int id, int isNew);

    Iterable<BreakdownConsultation> findAllByIsNew(int isNew);



    @Transactional
    void deleteById(int id);
}