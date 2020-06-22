package com.api.Poletechnika.repository;

import com.api.Poletechnika.models.AgronomyMachineryModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AgronomyMachineryModelRepository extends CrudRepository<AgronomyMachineryModel, Integer> {

    List<AgronomyMachineryModel> findAllByTypeId(String typeId);
    AgronomyMachineryModel findByTypeIdAndId(String typeId, int itemId);

    AgronomyMachineryModel findById(int itemId);

    @Transactional
    void deleteAgronomyMachineryModelByTypeIdAndId(String typeId, int itemId);
}