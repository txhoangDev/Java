package org.database.example.mongodbexample;

import org.springframework.boot.SpringApplication;

public class TestMongodbExampleApplication {

    public static void main(String[] args) {
        SpringApplication.from(MongodbExampleApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
