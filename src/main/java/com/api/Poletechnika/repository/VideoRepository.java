package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.Policy;
import com.api.Poletechnika.models.Video;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface VideoRepository extends CrudRepository<Video, Integer> {

    Video findById(int id);

    @Transactional
    void deleteById(int id);
}