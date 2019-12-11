package hkr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import server.EnableRpc;

@SpringBootApplication
@EnableRpc
public class Application {

    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}
