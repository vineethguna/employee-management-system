package com.vineeth.ems.entities.repository;

import com.vineeth.ems.entities.Counter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;

@Repository
public interface CounterRepository extends JpaRepository<Counter, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Counter findByUsername(String username);
}
