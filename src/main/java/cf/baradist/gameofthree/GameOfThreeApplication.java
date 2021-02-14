package cf.baradist.gameofthree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GameOfThreeApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameOfThreeApplication.class, args);
    }

}
