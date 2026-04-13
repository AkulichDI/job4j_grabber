package ru.job4j.grabber.service;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final Logger log = Logger.getLogger(Config.class);
    private final Properties properties = new Properties();


    public void load (String file ){

        try(InputStream in = Config.class.getClassLoader().getResourceAsStream(file)){
            if (in == null) {
                throw new IllegalArgumentException("Resource not found: " + file);
            }
            properties.load(in);
        }catch (IOException | IllegalArgumentException e ){
            log.error(String.format("When load file : %s", file), e);
        }

    }


    public String get ( String key){

        return properties.getProperty(key);

    }




}
