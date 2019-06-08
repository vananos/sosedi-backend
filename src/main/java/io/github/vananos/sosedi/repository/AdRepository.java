package io.github.vananos.sosedi.repository;

import io.github.vananos.sosedi.models.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdRepository extends JpaRepository<Advertisement, Long> {
}
