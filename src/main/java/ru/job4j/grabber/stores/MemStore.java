package ru.job4j.grabber.stores;

import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.service.Store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class MemStore implements Store {

    private final AtomicLong ids = new AtomicLong(1);
    private final Map<Long, Post> postsById = new HashMap<>();
    private final Map<String, Long> idsByLink = new HashMap<>();


    @Override
    public void save(Post post) {
        Long existingId = idsByLink.get(post.getLink());
        if (existingId != null) {
            post.setId(existingId);
        } else if (post.getId() == null) {
            post.setId(ids.getAndIncrement());
        }
        postsById.put(post.getId(), post);
        idsByLink.put(post.getLink(), post.getId());
    }

    @Override
    public List<Post> getAll() {
        return new ArrayList<>(postsById.values());
    }

    @Override
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(postsById.get(id));
    }
}
