package com.harena.api.service;

import static com.harena.api.file.FileHashAlgorithm.NONE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.harena.api.conf.FacadeIT;
import com.harena.api.file.BucketComponent;
import com.harena.api.file.FileHash;
import com.harena.api.utils.PageRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.patrimoine.cas.PatrimoineRicheMoyenCas;
import school.hei.patrimoine.cas.zety.PatrimoineZetyAu3Juillet2024;
import school.hei.patrimoine.modele.Patrimoine;
import school.hei.patrimoine.serialisation.Serialiseur;

class PatrimoineServiceIT extends FacadeIT {
  @MockBean BucketComponent bucketComponent;
  @Autowired PatrimoineService subject;
  private final PatrimoineZetyAu3Juillet2024 patrimoineZetySupplier =
      new PatrimoineZetyAu3Juillet2024();
  private final Serialiseur<Patrimoine> serializer = new Serialiseur<>();

  @Test
  void patrimoine_service() throws IOException {
    Path tempFile = Files.createTempFile("test_patrimoine_service", null);
    Files.writeString(tempFile, serializer.serialise(patrimoineZetySupplier.get()));
    when(bucketComponent.download(any())).thenReturn(tempFile.toFile());
    when(bucketComponent.upload(any(), any())).thenReturn(new FileHash(NONE, "hash"));

    assertFalse(subject.getAllPatrimoine(PageRequest.of(4, 10)).isEmpty());
    assertDoesNotThrow(
        () -> {
          subject.getPatrimone("test");
        });
    Patrimoine moyenCas = new PatrimoineRicheMoyenCas().get();
    assertDoesNotThrow(() -> subject.savePatrimoines(List.of(moyenCas)));
  }
}
