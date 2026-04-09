package ru.job4j.quartz.stores;

import org.apache.log4j.Logger;
import ru.job4j.quartz.model.Post;
import ru.job4j.quartz.service.SchedulerManager;
import ru.job4j.quartz.service.Store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

        try( PreparedStatement statement = connection.prepareStatement("INTO INSERT posts ( title, link, description, time) VALUES ( ?, ?, ?, ? )")){

            statement.setString(1,post.getTitle());
            statement.setString(2, post.getLink());
            statement.setString(3, post.getDescription());
            statement.setLong(4, post.getTime());

            statement.execute();
        }catch ( SQLException sqlException){
            log.error("When save Post to dataBase ", sqlException);
        }


    }

    @Override
    public List<Post> getAll() {

        List<Post> result = new ArrayList<>();

        try ( PreparedStatement statement = connection.prepareStatement("SELECT * FROM posts" )){
            try(ResultSet resultSet = statement.executeQuery()){

                while (resultSet.next()){

                    result.add(new Post(
                        resultSet.getLong("id"),
                        resultSet.getString("title"),
                        resultSet.getString("link"),
                        resultSet.getString("description"),
                        resultSet.getLong("created")
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

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM posts WHERE id = ? ")){

            statement.setLong(1, id);
            try( ResultSet resultSet = statement.executeQuery() ){

                result = Optional.of(new Post(
                        resultSet.getLong("id"),
                        resultSet.getString("title"),
                        resultSet.getString("link"),
                        resultSet.getString("description"),
                        resultSet.getLong("created")
                ));

            }

        }catch ( SQLException e){

            log.error("When findById Posts from dataBase ", e);

        }

        return result;
    }
}
