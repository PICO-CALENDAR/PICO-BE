package com.pico.server.repository;

import com.pico.server.entity.Memorybox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoryboxRepository extends JpaRepository<Memorybox, Long> {

}
