package com.harena.api.service;

import com.harena.api.utils.StringNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.patrimoine.modele.EvolutionPatrimoine;
import school.hei.patrimoine.modele.FluxJournalier;
import school.hei.patrimoine.modele.Patrimoine;
import school.hei.patrimoine.visualisation.xchart.GrapheurEvolutionPatrimoine;

import java.io.File;
import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProjectionFutureService {
  private final PatrimoineService patrimoineService;

  public Set<FluxJournalier> getFluxJournaliers(
      String patrimoineName, LocalDate startDate, LocalDate endDate) {
    EvolutionPatrimoine evolutionPatrimoine =
        getEvolutionPatrimoine(patrimoineName, startDate, endDate);

    return evolutionPatrimoine.getFluxJournaliers();
  }

  public File getGraph(String patrimoineName, LocalDate startDate, LocalDate endDate) {
    EvolutionPatrimoine evolutionPatrimoine =
        getEvolutionPatrimoine(patrimoineName, startDate, endDate);
    return new GrapheurEvolutionPatrimoine().apply(evolutionPatrimoine);
  }

  private EvolutionPatrimoine getEvolutionPatrimoine(
      String patrimoineName, LocalDate startDate, LocalDate endDate) {
    Patrimoine patrimoine = patrimoineService.getPatrimone(StringNormalizer.apply(patrimoineName));
    return new EvolutionPatrimoine(patrimoine.nom(), patrimoine, startDate, endDate);
  }
}
