package com.megthink.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.model.Author;

@Repository("authorRepository")
public interface AuthorRepository extends JpaRepository<Author, Integer> {

}
