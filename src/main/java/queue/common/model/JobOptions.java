package queue.common.model;

import java.util.Date;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@Getter
@Setter
public class JobOptions {

  private long wait;

  private Date at;

  private String queueOn;
}
