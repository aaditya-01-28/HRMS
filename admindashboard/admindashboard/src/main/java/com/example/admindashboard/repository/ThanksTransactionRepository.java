package com.example.admindashboard.repository;

import com.example.admindashboard.model.ThanksTransaction;
import com.example.admindashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;

@Repository
public interface ThanksTransactionRepository extends JpaRepository<ThanksTransaction, Long> {
    // Fetches the ledger history, newest first
    List<ThanksTransaction> findByUserOrderByTransactionDateDesc(User user);

    @Query("SELECT t FROM ThanksTransaction t WHERE t.user = :user " +
           "AND (cast(:startDate as date) IS NULL OR t.transactionDate >= :startDate) " +
           "AND (cast(:endDate as date) IS NULL OR t.transactionDate <= :endDate) " +
           "AND (cast(:searchQuery as text) IS NULL OR LOWER(t.description) LIKE cast(:searchQuery as text) OR LOWER(t.transactionType) LIKE cast(:searchQuery as text)) " +
           "ORDER BY t.transactionDate DESC, t.id DESC")
    List<ThanksTransaction> findFilteredTransactions(
            @Param("user") User user, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate, 
            @Param("searchQuery") String searchQuery);
}