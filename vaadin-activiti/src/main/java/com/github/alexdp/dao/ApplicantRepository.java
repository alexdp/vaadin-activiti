package com.github.alexdp.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.alexdp.model.Applicant;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {

}
