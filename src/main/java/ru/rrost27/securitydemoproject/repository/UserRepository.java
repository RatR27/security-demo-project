package ru.rrost27.securitydemoproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.rrost27.securitydemoproject.entity.MyUser;

@Repository
public interface UserRepository extends JpaRepository<MyUser, Long> {
    MyUser findByUsername(String username);
}
