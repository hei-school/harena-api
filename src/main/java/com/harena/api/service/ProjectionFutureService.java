package com.harena.api.service;

import com.harena.api.utils.StringNormalizer;
import java.io.File;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.patrimoine.modele.EvolutionPatrimoine;
import school.hei.patrimoine.modele.FluxImpossibles;
import school.hei.patrimoine.modele.Patrimoine;
import school.hei.patrimoine.visualisation.xchart.GrapheurEvolutionPatrimoine;

@Service
@RequiredArgsConstructor
public class ProjectionFutureService {
  private final PatrimoineService patrimoineService;

  public Set<FluxImpossibles> getFluxImpossibles(
      String patrimoineName, LocalDate startDate, LocalDate endDate) {
    EvolutionPatrimoine evolutionPatrimoine =
        getEvolutionPatrimoine(patrimoineName, startDate, endDate);

    return evolutionPatrimoine.getFluxImpossibles();
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
