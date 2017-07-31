package org.webant.queen.task.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.webant.queen.task.dao.TaskRepository;
import org.webant.queen.task.entity.Task;

import java.util.List;

@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    private TaskRepository repository;

    public Task get(String id) {
        return repository.findById(id).get();
    }

    public int save(List<Task> list) {
        if (list == null || list.isEmpty())
            return 0;

        Iterable<Task> affectRows = repository.saveAll(list);
        return list.size();
    }

    public String save(Task task) {
        if (task == null || StringUtils.isEmpty(task.getFingerPrint()))
            return "";

        List<Task> list = repository.findAllByFingerPrint(task.getFingerPrint());
        if (!list.isEmpty())
            return list.get(0).getId();

        Task save = repository.save(task);
        return save.getId();
    }
}