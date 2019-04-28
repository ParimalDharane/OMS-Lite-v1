package com.parimal.omslite.repo;

import com.parimal.omslite.entities.Execution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExecutionRepository extends JpaRepository<Execution, Long> {

    List<Execution> findAllByBookId(Long bookId);

    @Query(value="SELECT TOP 1 * FROM execution_table e WHERE e.bookId = ?1",
            nativeQuery = true)
    Execution getFirstExecution(long bookId);
}
