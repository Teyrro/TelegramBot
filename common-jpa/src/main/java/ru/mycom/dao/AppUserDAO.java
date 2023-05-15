package ru.mycom.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mycom.entity.AppUser;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByTelegramUserId(Long id);
}
