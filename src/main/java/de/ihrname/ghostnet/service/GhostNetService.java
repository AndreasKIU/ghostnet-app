package de.ihrname.ghostnet.service;

import de.ihrname.ghostnet.domain.*;
import de.ihrname.ghostnet.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service @RequiredArgsConstructor @Transactional
public class GhostNetService {
  private final GhostNetRepo nets;
  private final PersonRepo people;

  // Netz melden (anonym möglich) -> immer REPORTED
  public GhostNet report(GhostNet net, Long reporterId) {
    net.setStatus(NetStatus.REPORTED);
    net.setAssignee(null);
    if (reporterId != null) {
      people.findById(reporterId).ifPresent(net::setReportedBy);
    }
    return nets.save(net);
  }

  // Liste nach Status-Filter
  public List<GhostNet> listByStatuses(Set<NetStatus> statuses) {
    return nets.findByStatusInOrderByCreatedAtAsc(statuses);
  }

  // Bergung übernehmen -> nur wenn REPORTED
  public GhostNet assign(Long netId, Long personId) {
    var net = nets.findById(netId).orElseThrow();
    var p = people.findById(personId).orElseThrow();

    if (net.getStatus() == NetStatus.MISSING)
      throw new IllegalStateException("Verschollene Netze können nicht zugewiesen werden.");
    if (net.getStatus() == NetStatus.RECOVERED)
      throw new IllegalStateException("Bereits geborgen.");
    if (net.getStatus() != NetStatus.REPORTED)
      throw new IllegalStateException("Nur gemeldete Netze können übernommen werden.");
    if (net.getAssignee() != null)
      throw new IllegalStateException("Bereits übernommen.");
    if (p.getRole() != Role.RECOVERER)
      throw new IllegalStateException("Nur RECOVERER dürfen übernehmen.");

    net.setAssignee(p);
    net.setStatus(NetStatus.SCHEDULED);
    return net;
  }

  // Zuweisung aufheben -> nur wenn SCHEDULED; nur Assignee selbst
  public GhostNet unassign(Long netId, Long personId) {
    var net = nets.findById(netId).orElseThrow();
    if (net.getStatus() != NetStatus.SCHEDULED || net.getAssignee() == null)
      throw new IllegalStateException("Nur geplante Netze mit Assignee können freigegeben werden.");
    if (!net.getAssignee().getId().equals(personId))
      throw new IllegalStateException("Nur die zugewiesene Person kann freigeben.");
    net.setAssignee(null);
    net.setStatus(NetStatus.REPORTED);
    return net;
  }

  // Als geborgen melden -> nur wenn SCHEDULED; nur Assignee
  public GhostNet markRecovered(Long netId, Long personId) {
    var net = nets.findById(netId).orElseThrow();
    if (net.getStatus() != NetStatus.SCHEDULED || net.getAssignee() == null)
      throw new IllegalStateException("Nur geplante Netze mit Assignee können als geborgen gemeldet werden.");
    if (!net.getAssignee().getId().equals(personId))
      throw new IllegalStateException("Nur die zugewiesene Person darf abschließen.");
    net.setStatus(NetStatus.RECOVERED);
    return net;
  }

  // Verschollen melden -> nur von REPORTED oder SCHEDULED; setzt Assignee auf null
  public GhostNet markMissing(Long netId, Long reporterId) {
    var net = nets.findById(netId).orElseThrow();
    var rep = people.findById(reporterId).orElseThrow();
    if (rep.getRole() != Role.REPORTER) throw new IllegalStateException("Nur REPORTER dürfen 'verschollen' melden.");
    if (rep.getPhone() == null || rep.getPhone().isBlank())
      throw new IllegalStateException("Für 'verschollen melden' ist eine Telefonnummer am Reporter erforderlich.");
    if (net.getStatus() != NetStatus.REPORTED && net.getStatus() != NetStatus.SCHEDULED)
      throw new IllegalStateException("Nur gemeldete oder geplante Netze können als verschollen gemeldet werden.");
    net.setAssignee(null); // geplante Netze verlieren Zuweisung
    net.setReportedBy(rep);
    net.setStatus(NetStatus.MISSING);
    return net;
  }

  // Wiedergefunden -> nur wenn MISSING -> zurück auf REPORTED
  public GhostNet reopen(Long netId, Long reporterId) {
    var net = nets.findById(netId).orElseThrow();
    var rep = people.findById(reporterId).orElseThrow();
    if (net.getStatus() != NetStatus.MISSING)
      throw new IllegalStateException("Nur verschollene Netze können wieder geöffnet werden.");
    if (rep.getRole() != Role.REPORTER) throw new IllegalStateException("Nur REPORTER dürfen wieder öffnen.");
    if (rep.getPhone() == null || rep.getPhone().isBlank())
      throw new IllegalStateException("Für das Wiederöffnen ist eine Telefonnummer am Reporter erforderlich.");
    net.setStatus(NetStatus.REPORTED);
    net.setAssignee(null);
    net.setReportedBy(rep);
    return net;
  }
}
