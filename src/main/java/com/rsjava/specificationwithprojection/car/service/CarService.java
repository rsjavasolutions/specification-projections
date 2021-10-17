package com.rsjava.specificationwithprojection.car.service;


import com.rsjava.specificationwithprojection.car.CarRepository;
import com.rsjava.specificationwithprojection.car.exception.CarNotFoundException;
import com.rsjava.specificationwithprojection.car.mapper.CarMapper;
import com.rsjava.specificationwithprojection.car.model.CarBrandAndPrice;
import com.rsjava.specificationwithprojection.car.model.CarEntity;
import com.rsjava.specificationwithprojection.car.model.CarModelOnly;
import com.rsjava.specificationwithprojection.car.model.CarUuidOnly;
import com.rsjava.specificationwithprojection.car.request.CarRequest;
import com.rsjava.specificationwithprojection.car.response.CarResponse;
import com.rsjava.specificationwithprojection.utils.PredicatesBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.rsjava.specificationwithprojection.car.mapper.CarMapper.mapToEntity;
import static com.rsjava.specificationwithprojection.car.mapper.CarMapper.mapToResponse;


@Slf4j
@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    @Transactional
    public List<CarResponse> getCars(String uuid,
                                     String brand,
                                     String model,
                                     Integer yearFrom,
                                     Integer yearTo,
                                     BigDecimal priceFrom,
                                     BigDecimal priceTo
    ) {
        Specification<CarEntity> specification = getCarEntityQuery(uuid, brand, model, yearFrom, yearTo, priceFrom, priceTo);

        return carRepository.findAll(specification)
                .stream()
                .map(CarMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<String> getCarModels() {
        return carRepository.findAllProjectedBy(CarModelOnly.class)
                .stream()
                .map(CarModelOnly::getModel)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<String> getCarUuids() {
        return carRepository.findAllProjectedBy(CarUuidOnly.class)
                .stream()
                .map(CarUuidOnly::getUuid)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<String> getCarBrandsAndPrices() {
        return carRepository.findAllProjectedBy(CarBrandAndPrice.class)
                .stream()
                .map(CarBrandAndPrice::getBrandAndPrice)
                .collect(Collectors.toList());
    }

    @Transactional
    public CarResponse getCar(String uuid) {
        CarEntity carEntity = carRepository.findByUuid(uuid).orElseThrow(() -> new CarNotFoundException(uuid));
        return mapToResponse(carEntity);
    }

    @Transactional
    public String saveCar(CarRequest request) {
        log.debug("Save car request with params: {}", request);

        return carRepository.save(mapToEntity(request)).getUuid();
    }

    @Transactional
    public CarResponse updateCar(String uuid, CarRequest request) {
        CarEntity carEntity = carRepository.findByUuid(uuid).orElseThrow(() -> new CarNotFoundException(uuid));

        carEntity.setBrand(request.getBrand());
        carEntity.setModel(request.getModel());

        return mapToResponse(carEntity);
    }

    @Transactional
    public void deleteCar(String uuid) {
        carRepository.deleteByUuid(uuid);
    }

    private Specification<CarEntity> getCarEntityQuery(String uuid,
                                                       String brand,
                                                       String model,
                                                       Integer yearFrom,
                                                       Integer yearTo,
                                                       BigDecimal priceFrom,
                                                       BigDecimal priceTo) {
        return (root, query, criteriaBuilder) ->
                new PredicatesBuilder<>(root, criteriaBuilder)
                        .caseInsensitiveLike(CarEntity.Fields.uuid, uuid)
                        .caseInsensitiveLike(CarEntity.Fields.brand, brand)
                        .caseInsensitiveLike(CarEntity.Fields.model, model)
                        .greaterThanOrEqualTo(root.get(CarEntity.Fields.year), yearFrom)
                        .lessThanOrEqualTo(root.get(CarEntity.Fields.year), yearTo)
                        .greaterThanOrEqualTo(root.get(CarEntity.Fields.price), priceFrom)
                        .lessThanOrEqualTo(root.get(CarEntity.Fields.price), priceTo)
                        .build();
    }
}
