package com.gustavofelix.rest_spring_boot.repository;

import com.gustavofelix.rest_spring_boot.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
