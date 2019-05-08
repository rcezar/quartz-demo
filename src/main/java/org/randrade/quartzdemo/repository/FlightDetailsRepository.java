package org.randrade.quartzdemo.repository;

import org.randrade.quartzdemo.entity.FlightDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightDetailsRepository extends JpaRepository<FlightDetailsEntity, Integer> {
}
