package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.Policy;
import com.api.Poletechnika.models.Video;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface VideoRepository extends CrudRepository<Video, Integer> {

    Video findById(int id);

    List<Video> findAllByBreakdownId(int id);

    @Transactional
    void deleteById(int id);
}