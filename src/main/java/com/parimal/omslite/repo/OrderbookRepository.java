package com.parimal.omslite.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.parimal.omslite.entities.Orderbook;

@Repository
public interface OrderbookRepository extends JpaRepository<Orderbook, Long> {

    Orderbook findByInstrumentName(String instrumentName);

}
