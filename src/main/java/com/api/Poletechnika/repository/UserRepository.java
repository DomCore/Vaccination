package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.Policy;
import com.api.Poletechnika.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    User findById(int id);
    User findUserByPhone(String phone);

    List<User> findAllByRegistrationStatus(String status);


    List<User> findAllByRegistrationStatusAndLicense(String status, String licenseType);

    List<User> findAllByLicense(String licenseType);


    User findUserByToken(String token);

    @Transactional
    void deleteById(int id);


}