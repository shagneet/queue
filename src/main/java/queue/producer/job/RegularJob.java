package queue.producer.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RegularJob extends Queueable{

  public void  handle(String[] args) throws Exception {

    log.info("regular");
  }

}
