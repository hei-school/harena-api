package com.harena.api.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.harena.api.conf.FacadeIT;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.patrimoine.cas.zety.PatrimoineZetyAu3Juillet2024;
import school.hei.patrimoine.modele.Devise;
import school.hei.patrimoine.modele.possession.Materiel;

class PossessionServiceIT extends FacadeIT {
  @MockBean PatrimoineService patrimoineService;
  private final PatrimoineZetyAu3Juillet2024 patrimoineDeZetySupplier =
      new PatrimoineZetyAu3Juillet2024();
  @Autowired PossessionService subject;

  @Test
  void possessions_patrimoine_test() {
    when(patrimoineService.getPatrimone(any())).thenReturn(patrimoineDeZetySupplier.get());
    when(patrimoineService.savePatrimoines(any()))
        .thenReturn(List.of(patrimoineDeZetySupplier.get()));

    assertFalse(subject.getPossessions("test").isEmpty());
    assertTrue(subject.getPossession("test", "espèces").isPresent());

    var euro = new Devise("euro", 20000, LocalDate.now(), -0.2);

    var testPossession =
        new Materiel("test", LocalDate.now(), 20000, LocalDate.now().minusDays(3), -0.2, euro);
    assertFalse(subject.savePossessions("test", List.of(testPossession)).isEmpty());

    assertTrue(subject.removePossession("test", "espèces").isPresent());
  }

  @Test
  void possessions_patrimoine_test_not_found_possessions() {
    when(patrimoineService.getPatrimone(any())).thenReturn(patrimoineDeZetySupplier.get());
    when(patrimoineService.savePatrimoines(any())).thenReturn(List.of());

    assertTrue(subject.removePossession("test", "test").isEmpty());
  }
}
