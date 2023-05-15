package ru.mycom.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mycom.entity.RawData;

public interface RawDataDAO extends JpaRepository<RawData, Long> {

}
