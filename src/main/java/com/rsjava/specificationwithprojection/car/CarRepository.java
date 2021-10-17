package com.rsjava.specificationwithprojection.car;

import com.rsjava.specificationwithprojection.car.model.CarEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CarRepository extends JpaRepository<CarEntity, Long> {

    Set<CarEntity> findAll(Specification<CarEntity> specification);

    <T> Collection<T> findAllProjectedBy(Class<T> type);

    Optional<CarEntity> findByUuid(String uuid);

    void deleteByUuid(String uuid);
}
