package de.ihrname.ghostnet;

import de.ihrname.ghostnet.domain.*;
import de.ihrname.ghostnet.repo.*;
import de.ihrname.ghostnet.service.GhostNetService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.*;

@Disabled("Superseded by GhostNetServiceRulesTest")

@SpringBootTest
class GhostNetServiceTest {
  @Autowired GhostNetService svc;
  @Autowired PersonRepo people;

  @Test void flow_report_assign_recover() {
    var rec = people.save(Person.builder().name("R").role(Role.RECOVERER).build());
    var net = GhostNet.builder().gpsLat(1.0).gpsLon(2.0).estimatedSize("klein").build();
    net = svc.report(net, null);
    assertThat(net.getStatus()).isEqualTo(NetStatus.REPORTED);

    net = svc.assign(net.getId(), rec.getId());
    assertThat(net.getStatus()).isEqualTo(NetStatus.SCHEDULED);
    assertThat(net.getAssignee().getId()).isEqualTo(rec.getId());

    net = svc.markRecovered(net.getId(), rec.getId());
    assertThat(net.getStatus()).isEqualTo(NetStatus.RECOVERED);
  }
}
