package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.Policy;
import com.api.Poletechnika.models.VideoFilter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface VideoFiltersRepository extends CrudRepository<VideoFilter, Integer> {

    VideoFilter findById(int id);

    List<VideoFilter> findAllByType(String type);

    //For subfilters
    List<VideoFilter> findAllByTypeAndParent(String type, int parent);

    @Transactional
    void deleteById(int id);
}