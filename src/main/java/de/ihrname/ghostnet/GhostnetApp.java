package de.ihrname.ghostnet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import de.ihrname.ghostnet.domain.*;
import de.ihrname.ghostnet.repo.*;

@SpringBootApplication
public class GhostnetApp {
  public static void main(String[] args) {
    SpringApplication.run(GhostnetApp.class, args);
  }

  @Bean CommandLineRunner seed(PersonRepo people, GhostNetRepo nets) {
    return args -> {
      if (people.count()==0) {
        var r1 = people.save(Person.builder().name("Taucherin Mia").role(Role.RECOVERER).phone("+49 170 1111").build());
        var r2 = people.save(Person.builder().name("Taucher Tom").role(Role.RECOVERER).phone("+49 170 2222").build());
        var rep = people.save(Person.builder().name("Küstenwache").role(Role.REPORTER).phone("+49 30 1234").build());
        nets.save(GhostNet.builder().gpsLat(54.10).gpsLon(13.40).estimatedSize("mittel").status(NetStatus.REPORTED).reportedBy(rep).build());
        nets.save(GhostNet.builder().gpsLat(54.12).gpsLon(13.45).estimatedSize("groß").status(NetStatus.REPORTED).build());
      }
    };
  }
}
