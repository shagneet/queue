package queue.producer.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Random;
@Slf4j
@Component
public class DoSomeStuff extends Queueable{
  public void  handle(String[] args) throws Exception {
    Random random = new Random();
    float randomValue = random.nextFloat(1);
    if(randomValue>0.8){
      throw new Exception("Oh no!");
    }
    log.info("HIIII "+args[0]+" and "+args[1]);
   }
}
