package com.harena.api.service;

import com.harena.api.utils.StringNormalizer;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.patrimoine.modele.Patrimoine;
import school.hei.patrimoine.modele.possession.Possession;

@Service
@RequiredArgsConstructor
public class PossessionService {
  private final PatrimoineService patrimoineService;

  public List<Possession> getPossessions(String patrimoineName) {
    String patrimoineId = StringNormalizer.apply(patrimoineName);
    Patrimoine patrimoine = patrimoineService.getPatrimone(patrimoineId);

    return patrimoine.possessions().stream().toList();
  }

  public Optional<Possession> getPossession(String patrimoineName, String possessionName) {
    String patrimoineId = StringNormalizer.apply(patrimoineName);
    String possessionId = StringNormalizer.apply(possessionName);
    Patrimoine patrimoine = patrimoineService.getPatrimone(patrimoineId);
    HashMap<String, Possession> possessions = new HashMap<>();
    patrimoine
        .possessions()
        .forEach(
            possession -> possessions.put(StringNormalizer.apply(possession.getNom()), possession));
    return Optional.ofNullable(possessions.get(possessionId));
  }

  public List<Possession> savePossessions(
      String patrimoineName, List<Possession> toSavePossessions) {
    Patrimoine patrimoine = patrimoineService.getPatrimone(StringNormalizer.apply(patrimoineName));

    HashMap<String, Possession> possessions = retrievePossessions(patrimoine);
    toSavePossessions.forEach(
        possession -> possessions.put(StringNormalizer.apply(possession.getNom()), possession));
    Patrimoine updatedPatrimoine =
        new Patrimoine(
            patrimoine.nom(),
            patrimoine.possesseur(),
            patrimoine.t(),
            new HashSet<>(possessions.values()));

    patrimoineService.savePatrimoines(List.of(updatedPatrimoine));
    return toSavePossessions;
  }

  public Optional<Possession> removePossession(String patrimoineName, String possessionName) {
    Patrimoine patrimoine = patrimoineService.getPatrimone(patrimoineName);

    HashMap<String, Possession> possessions = retrievePossessions(patrimoine);
    Optional<Possession> removedPossession =
        Optional.ofNullable(possessions.remove(StringNormalizer.apply(possessionName)));

    if (removedPossession.isEmpty()) {
      return Optional.empty();
    }

    patrimoineService.savePatrimoines(List.of(patrimoine));

    return removedPossession;
  }

  private HashMap<String, Possession> retrievePossessions(Patrimoine patrimoine) {
    HashMap<String, Possession> possessions = new HashMap<>();
    patrimoine
        .possessions()
        .forEach(
            possession -> possessions.put(StringNormalizer.apply(possession.getNom()), possession));
    return possessions;
  }
}
