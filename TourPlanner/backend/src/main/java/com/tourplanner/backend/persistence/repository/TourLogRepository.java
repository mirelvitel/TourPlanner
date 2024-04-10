package com.tourplanner.backend.persistence.repository;

import com.tourplanner.backend.persistence.entity.TourLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourLogRepository extends JpaRepository<TourLogEntity, Long> {

}

//The `TourLogRepository` interface serves as a repository layer for managing tour log data in the application's database. Extending `JpaRepository`,
// it inherits a comprehensive set of CRUD operations (Create, Read, Update, Delete) for `TourLogEntity` objects,
// leveraging Spring Data JPA to simplify data access and manipulation. This interface is designed to interact with tour logs,
// allowing the application to perform database operations such as retrieving, saving,
// updating, or deleting tour log records without the need for boilerplate code.