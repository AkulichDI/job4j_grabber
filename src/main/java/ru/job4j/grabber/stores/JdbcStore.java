package ru.job4j.grabber.stores;

import org.apache.log4j.Logger;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.service.Store;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcStore implements Store {
    private static final Logger log = Logger.getLogger(JdbcStore.class);
    private final Connection connection;

    public JdbcStore(Connection connection) {
        this.connection = connection;
    }


    @Override
    public void save(Post post) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO  posts (title, link, description, created) VALUES (?, ?, ?, ?)"
        )) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getLink());
            statement.setString(3, post.getDescription());
            statement.setTimestamp(4, new Timestamp(post.getTime()));
            statement.executeUpdate();
        } catch (SQLException sqlException) {
            log.error("When save Post to dataBase ", sqlException);
        }
    }

    @Override
    public List<Post> getAll() {

        List<Post> result = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM posts")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new Post(
                            resultSet.getLong("id"),
                            resultSet.getString("title"),
                            resultSet.getString("link"),
                            resultSet.getString("description"),
                            resultSet.getTimestamp("created").getTime()
                    ));
                }
            }
        } catch (SQLException e) {
            log.error("When getAll Posts from dataBase ", e);
        }
        return result;
    }

    @Override
    public Optional<Post> findById(Long id) {

        Optional<Post> result = Optional.empty();

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM posts WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    result = Optional.of(new Post(
                            resultSet.getLong("id"),
                            resultSet.getString("title"),
                            resultSet.getString("link"),
                            resultSet.getString("description"),
                            resultSet.getTimestamp("created").getTime()
                    ));
                }
            }
        } catch (SQLException e) {
            log.error("When findById Posts from dataBase ", e);
        }
        return result;
    }
}
