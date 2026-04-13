package ru.job4j.quartz;

import org.apache.log4j.Logger;
import ru.job4j.quartz.model.Post;
import ru.job4j.quartz.service.Config;
import ru.job4j.quartz.service.SchedulerManager;
import ru.job4j.quartz.service.SuperJobGrab;
import ru.job4j.quartz.stores.JdbcStore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Main {
    private static final Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        var config = new Config();
        config.load("application.properties");
        try {
            Class.forName(config.get("db.driver-class-name"));
        } catch (ClassNotFoundException e) {
            log.error("Driver class not found", e);
            return;
        }
        try (Connection connection = DriverManager.getConnection(
                config.get("db.url"),
                config.get("db.username"),
                config.get("db.password"));
             SchedulerManager scheduler = new SchedulerManager()) {
            var store = new JdbcStore(connection);
            scheduler.init();
            scheduler.load(
                    Integer.parseInt(config.get("rabbit.interval")),
                    SuperJobGrab.class,
                    store
            );
            Thread.sleep(10000);
        } catch (SQLException e) {
            log.error("When create a connection", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Main thread interrupted", e);
        }
    }
}
