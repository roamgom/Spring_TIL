package com.example.tutorial.payroll.repository;

import com.example.tutorial.payroll.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
