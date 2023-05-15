package ru.mycom.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mycom.entity.BinaryContent;

public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
}
