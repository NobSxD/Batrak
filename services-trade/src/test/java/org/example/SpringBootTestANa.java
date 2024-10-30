package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringBootTestANa {

    @LocalServerPort
    private Integer port;

    @Test
    public void shouldDisplayBook() {
        String s = "http://localhost:" + port;
        System.out.println(s);

    }
}
