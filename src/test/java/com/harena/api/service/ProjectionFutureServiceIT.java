package com.harena.api.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.harena.api.conf.FacadeIT;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.patrimoine.cas.zety.PatrimoineZetyAu3Juillet2024;

public class ProjectionFutureServiceIT extends FacadeIT {
  @MockBean PatrimoineService patrimoineService;
  @Autowired ProjectionFutureService subject;
  private final PatrimoineZetyAu3Juillet2024 patrimoineSupplier =
      new PatrimoineZetyAu3Juillet2024();

  @Test
  void projection_future_service_test() {
    when(patrimoineService.getPatrimone(any())).thenReturn(patrimoineSupplier.get());
    var startDate = LocalDate.of(2024, 7, 27);
    var endDate = LocalDate.of(2034, 2, 16);

    assertFalse(subject.getFluxJournaliers("test", startDate, endDate).isEmpty());

    assertTrue(subject.getGraph("test", startDate, endDate).exists());
  }
}
