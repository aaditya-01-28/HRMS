package com.example.admindashboard.repository;

import com.example.admindashboard.model.ThanksCartItem;
import com.example.admindashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThanksCartItemRepository extends JpaRepository<ThanksCartItem, Long> {
    List<ThanksCartItem> findByUser(User user);
    void deleteByUser(User user);
}
