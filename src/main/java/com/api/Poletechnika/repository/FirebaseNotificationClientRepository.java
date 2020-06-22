package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.FirebaseNotificationClient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FirebaseNotificationClientRepository extends CrudRepository<FirebaseNotificationClient, Integer> {

    FirebaseNotificationClient findByToken(String token);

    FirebaseNotificationClient findByUserId(int user_id);

    Iterable<FirebaseNotificationClient> findAllByUserId(int user_id);

    @Transactional
    void deleteById(int id);
}