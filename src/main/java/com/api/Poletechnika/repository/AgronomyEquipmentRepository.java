package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.AgronomyEquipment;
import com.api.Poletechnika.models.AgronomyMachinery;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AgronomyEquipmentRepository extends CrudRepository<AgronomyEquipment, Integer> {
    AgronomyEquipment findById(int id);
    List<AgronomyEquipment> findByParentId(String parentId);


    @Transactional
    void deleteById(int itemId);
}