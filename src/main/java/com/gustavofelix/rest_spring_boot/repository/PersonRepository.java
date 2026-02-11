package com.gustavofelix.rest_spring_boot.repository;

import com.gustavofelix.rest_spring_boot.model.Person;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonRepository extends JpaRepository<Person, Long> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Person p SET p.enabled = false WHERE p.id =:id")
    void disablePerson(@Param("id") Long id);

    @Query("SELECT p FROM Person p WHERE p.firstName LIKE LOWER(CONCAT('%', :firstName, '%'))")
    Page<Person> findPeopleByName(@Param("firstName") String firstName, Pageable pageable);
}
