package me.lin.mall.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MallAuthServerApplicationTests {

    @Test
    void contextLoads() {
        new dog();
    }

    public class Animal {

        String name;

        public Animal() {
        }

        public Animal(String name) {
            this.name = name;
        }
    }

    public class dog extends Animal {

        public dog() {
        }

        public dog(String name) {
            super(name);
        }

        public String eat(String eat) {
            return eat;
        }
    }

}
