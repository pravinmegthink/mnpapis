package com.megthink.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megthink.gateway.model.Author;
import com.megthink.gateway.repository.AuthorRepository;

@Service("authorService")
public class AuthorService {

	private AuthorRepository authorRepository;

	@Autowired
	public AuthorService(AuthorRepository authorRepository) {
		this.authorRepository = authorRepository;
	}

	public Author saveAuthor(Author data) {
		return authorRepository.save(data);
	}
}