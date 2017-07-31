package org.webant.queen.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.webant.queen.data.entity.Data;

@Repository
public interface DataRepository extends JpaRepository<Data, String> {
}
