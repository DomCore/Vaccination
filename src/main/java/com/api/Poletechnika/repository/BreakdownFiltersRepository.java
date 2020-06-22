package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.Breakdown;
import com.api.Poletechnika.models.BreakdownFilter;
import com.api.Poletechnika.models.VideoFilter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BreakdownFiltersRepository extends CrudRepository<BreakdownFilter, Integer> {

    BreakdownFilter findById(int id);

    List<BreakdownFilter> findAllByType(String type);

    BreakdownFilter findByTitleAndType(String title, String type);

    BreakdownFilter findByValueIdAndType(String valueId, String type);

    @Transactional
    void deleteById(int id);
}