package io.ugurh.oauth2.dummy_data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.ugurh.oauth2.glee.models.Glee;
import io.ugurh.oauth2.glee.repository.GleeRepository;
import io.ugurh.oauth2.user.models.UserRole;
import io.ugurh.oauth2.user.models.User;
import io.ugurh.oauth2.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
@Slf4j
public class LoadDatabase {

    @Value("${data.users:admin,userman,user1,user2,user3,user4,user5}")
    private String[] users;
    @Value("${data.nouns:sky,winter,sun,moon,spring,weekend,fall,summer,mountain,wolf,bird}")
    private String[] nouns;
    @Value("${data.verbs:falling,rising,exploding,shining,coming,lightning,howling,chirping}")
    private String[] verbs;
    @Value("${data.feelings:bad,numb,fine,good}")
    private String[] feelings;
    @Value("${data.timeOfDay:morning,afternoon,evening,night}")
    private String[] timeOfDay;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("clientSecret:secret")
    private String clientSecret;

    int rnd(int size) {
        return (int) (Math.random() * size);
    }
  
    @Bean
    CommandLineRunner initUsers(UserRepository repo) {

        return args -> {
            for (int i = 0; i < users.length; i++) {
                String email = users[i] + "@" + users[i] + ".com";
                UserRole role = i > 1 ? UserRole.USER : i == 0 ? UserRole.ADMIN : UserRole.USER_MANAGER;
                double minGleePerDay = rnd(1000 * feelings.length);
                String pwd = passwordEncoder.encode("pwd");
                log.info("save {}", repo.save(new User(null, email, pwd, role, minGleePerDay, null)));
            }
        };
    }

    @Bean
    CommandLineRunner initGlee(GleeRepository repo, UserRepository userRepo) {
        return args -> {
            for (int i = 0; i < 100; i++) {
                LocalDate date = LocalDate.ofEpochDay((long) (10_000L + Math.random() * 10_000));
                LocalTime time = LocalTime.ofSecondOfDay((long) (Math.random() * 24 * 3600));
                int u = rnd(users.length);
                double value = rnd(1000 * feelings.length);
                String text = "This " + timeOfDay[(int) (((time.getHour() + 18) % 24) * timeOfDay.length / 24.0)] + " the " + nouns[rnd(nouns.length)] + " is " + verbs[rnd(verbs.length)] + " and I'm feeling " + feelings[(int) (value / 1000)];
                User user = userRepo.findByEmail(users[u] + "@" + users[u] + ".com").orElse(userRepo.findAll().iterator().next());
                log.info("save {}", repo.save(new Glee(null, date, time, text, value, user)));
            }
        };
    }

}