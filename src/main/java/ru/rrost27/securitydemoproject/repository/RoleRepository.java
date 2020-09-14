package ru.rrost27.securitydemoproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.rrost27.securitydemoproject.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
