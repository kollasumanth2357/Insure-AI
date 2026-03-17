package com.insure.insurebackend.repository;

import com.insure.insurebackend.model.Document;
import com.insure.insurebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByUser(User user);
}