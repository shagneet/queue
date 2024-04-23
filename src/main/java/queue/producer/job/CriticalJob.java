package queue.producer.job;

import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

  @Slf4j
  @Component
  public class CriticalJob extends Queueable{

    public void  handle(String[] args) throws Exception {
      log.info("critical");
    }
    @Override
    public void queueOn(){
      this.queue="critical";
    }
  }
