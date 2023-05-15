package ru.mycom.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mycom.entity.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
