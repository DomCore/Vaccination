package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.User;
import com.api.Poletechnika.models.UserGlobalData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserGlobalRepository extends CrudRepository<UserGlobalData, Integer> {

    UserGlobalData findById(int id);

}