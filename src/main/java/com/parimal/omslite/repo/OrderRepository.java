package com.parimal.omslite.repo;

import com.parimal.omslite.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByBookId(Long bookId);

    Order findByBookIdAndId(Long bookId, Long id);

    @Query(value = "SELECT * FROM order_table o WHERE o.bookId = ?1 AND o.openQuantity > 0 AND (o.marketOrder = true OR o.price >= ?2)",
            nativeQuery = true)
    List<Order> findAllEligible(long bookId, double price);

    @Query(
            value = "SELECT * FROM order_table o WHERE o.bookId = ?1 AND o.valid = true AND o.openQuantity > 0 AND o.marketOrder = false AND o.price < ?2",
            nativeQuery = true)
    List<Order> findAllNonEligible(long bookId, double price);

}
