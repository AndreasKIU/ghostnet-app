package de.ihrname.ghostnet;

import de.ihrname.ghostnet.domain.*;
import de.ihrname.ghostnet.repo.*;
import de.ihrname.ghostnet.service.GhostNetService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GhostNetServiceRulesTest {

  @Autowired GhostNetService svc;
  @Autowired GhostNetRepo nets;
  @Autowired PersonRepo people;

  Person reporter;
  Person diver;

  @BeforeEach
  void setup() {
    nets.deleteAll();
    people.deleteAll();
    reporter = people.save(Person.builder()
        .name("Küstenwache").phone("+49 30 1234").role(Role.REPORTER).build());
    diver = people.save(Person.builder()
        .name("Taucherin Mia").phone("+49 170 1111").role(Role.RECOVERER).build());
  }

  @Test
  void missing_then_no_assign_or_recover() {
    GhostNet n = nets.save(GhostNet.builder()
        .gpsLat(54.1).gpsLon(13.4).estimatedSize("mittel").status(NetStatus.REPORTED).build());
    svc.markMissing(n.getId(), reporter.getId());

    var ex1 = assertThrows(IllegalStateException.class,
        () -> svc.assign(n.getId(), diver.getId()));
    assertTrue(ex1.getMessage().toLowerCase().contains("verschollene"));

    var ex2 = assertThrows(IllegalStateException.class,
        () -> svc.markRecovered(n.getId(), diver.getId()));
    assertTrue(ex2.getMessage().toLowerCase().contains("nur geplante netze"));
  }

  @Test
  void reopen_then_assign_and_recover_happy_path() {
    GhostNet n = nets.save(GhostNet.builder()
        .gpsLat(54.12).gpsLon(13.45).estimatedSize("groß").status(NetStatus.REPORTED).build());
    svc.markMissing(n.getId(), reporter.getId());

    svc.reopen(n.getId(), reporter.getId());
    assertEquals(NetStatus.REPORTED, nets.findById(n.getId()).orElseThrow().getStatus());

    svc.assign(n.getId(), diver.getId());
    assertEquals(NetStatus.SCHEDULED, nets.findById(n.getId()).orElseThrow().getStatus());

    svc.markRecovered(n.getId(), diver.getId());
    assertEquals(NetStatus.RECOVERED, nets.findById(n.getId()).orElseThrow().getStatus());
  }

  @Test
  void bean_validation_on_lat_lon() {
    GhostNet invalid = GhostNet.builder()
        .gpsLat(1234.0)      // ungültig
        .gpsLon(-999.0)      // ungültig
        .estimatedSize("klein")
        .status(NetStatus.REPORTED)
        .build();

    try (ValidatorFactory vf = Validation.buildDefaultValidatorFactory()) {
      Validator validator = vf.getValidator();
      var violations = validator.validate(invalid);
      assertTrue(violations.size() >= 2, "Expected range violations for lat/lon");
    }
  }
}
