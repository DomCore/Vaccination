package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.UserPermission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPermissionRepository extends CrudRepository<UserPermission, Integer> {

    //UserPermission findByUser_id(int user_id);
    UserPermission findByUserId(int userId);
}