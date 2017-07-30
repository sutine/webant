package org.webant.queen.task.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.webant.queen.task.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
}
