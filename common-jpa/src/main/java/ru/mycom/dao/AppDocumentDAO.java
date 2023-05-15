package ru.mycom.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mycom.entity.AppDocument;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
