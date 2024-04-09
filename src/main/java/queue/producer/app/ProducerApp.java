package queue.producer.app;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Profile("producer")
@SpringBootApplication(scanBasePackages= {"queue.producer", "queue.common"})
@EnableJpaRepositories({"queue.producer", "queue.common"})
@EntityScan({"queue.producer", "queue.common"})
@EnableJpaAuditing
public class ProducerApp {

  public static void main(String[] args) {
    SpringApplication.run(ProducerApp.class, args);
  }
}
