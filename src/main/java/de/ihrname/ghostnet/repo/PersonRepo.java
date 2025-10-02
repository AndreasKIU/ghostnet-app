package de.ihrname.ghostnet.repo;

import de.ihrname.ghostnet.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepo extends JpaRepository<Person, Long> {}
