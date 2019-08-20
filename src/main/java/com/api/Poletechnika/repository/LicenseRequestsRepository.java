package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.License;
import com.api.Poletechnika.models.LicenseRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LicenseRequestsRepository extends CrudRepository<LicenseRequest, Integer> {
    LicenseRequest findById(int id);

    //for user controller
    LicenseRequest findTopByUserId(int id);

    List<LicenseRequest> findAllByUserId(int id);



    @Transactional
    void deleteAllByUserId(int user_id);
}