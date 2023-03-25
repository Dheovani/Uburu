package br.com.uburu.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.uburu.spring.entity.Path;

@Repository
public interface PathRepository extends JpaRepository<Path, Long> {}
