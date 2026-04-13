package ru.job4j.grabber.stores;

import org.apache.log4j.Logger;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.service.Store;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcStore implements Store {
    private static final Logger log = Logger.getLogger(JdbcStore.class);
    private final Connection connection;

    public JdbcStore(Connection connection) {
        this.connection = connection;
        initTable();
    }

    private void initTable() {
        try (InputStream inputStream = JdbcStore.class.getClassLoader().getResourceAsStream("db/init.sql")) {
            if (inputStream == null) {
                throw new IllegalStateException("Resource not found: db/init.sql");
            }
            String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        } catch (SQLException | IOException | IllegalStateException e) {
            log.error("When init database schema", e);
        }
    }


    @Override
    public void save(Post post) {
        try (PreparedStatement statement = connection.prepareStatement(
                """
                        INSERT INTO posts (title, link, description, created)
                        VALUES (?, ?, ?, ?)
                        ON CONFLICT (link)
                        DO UPDATE SET
                            title = EXCLUDED.title,
                            description = EXCLUDED.description,
                            created = EXCLUDED.created
                        """
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
