package ru.job4j.quartz.stores;

import ru.job4j.quartz.model.Post;
import ru.job4j.quartz.service.Store;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class JdbcStore implements Store {

    private final Connection connection;

    public JdbcStore(Connection connection) {
        this.connection = connection;
    }


    @Override
    public void save(Post post) {

    }

    @Override
    public List<Post> getAll() {
        return List.of();
    }

    @Override
    public Optional<Post> findById(Long id) {
        return Optional.empty();
    }
}
