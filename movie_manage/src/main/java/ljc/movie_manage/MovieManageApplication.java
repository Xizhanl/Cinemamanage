package ljc.movie_manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class MovieManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(MovieManageApplication.class, args);

    }

}
