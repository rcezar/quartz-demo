package org.randrade.quartzdemo.repository;

import org.randrade.quartzdemo.entity.FlightScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightScheduleRepository  extends JpaRepository<FlightScheduleEntity, Integer> {

}
