package com.harena.api.service;

import com.harena.api.exception.InternalServerErrorException;
import com.harena.api.exception.NotFoundException;
import com.harena.api.file.BucketComponent;
import com.harena.api.file.BucketConf;
import com.harena.api.utils.PageRequest;
import com.harena.api.utils.StringNormalizer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import school.hei.patrimoine.modele.Patrimoine;
import school.hei.patrimoine.serialisation.Serialiseur;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import static java.nio.file.Files.createTempDirectory;
import static java.nio.file.Files.createFile;

@Log
@Service
@RequiredArgsConstructor
public class PatrimoineService {
  private final String PATRIMOINE_KEY_PREFIX = "patrimoines/";
  private final BucketComponent bucketComponent;
  private final BucketConf bucketConf;
  private final Serialiseur<Patrimoine> serializer = new Serialiseur<>();

  public Patrimoine getPatrimone(String patrimoineName) {
    File patrimoineFile;
    try {
      patrimoineFile =
          bucketComponent.download(PATRIMOINE_KEY_PREFIX + StringNormalizer.apply(patrimoineName));
    } catch (SdkClientException e) {
      if (e.getMessage().contains("Unable to load credentials")) {
        throw new NotFoundException("Patrimoine " + patrimoineName + " not found");
      }
      throw e;
    }

    try {
      return serializer.deserialise(Files.readString(patrimoineFile.toPath()));
    } catch (IOException | ClassCastException e) {
      throw new InternalServerErrorException(
              "Could not read values of patrimoine " + patrimoineName);
    }
  }

  public List<Patrimoine> savePatrimoines(List<Patrimoine> toSavePatrimoines) {
    try {
      Path tempDir = createTempDirectory("harena-api-toSavePatrimoines");
      toSavePatrimoines.forEach(patrimoine -> writeFile(tempDir, patrimoine));

      bucketComponent.upload(tempDir.toFile(), PATRIMOINE_KEY_PREFIX);
    } catch (IOException e) {
      throw new InternalServerErrorException("Unable to save all objects");
    }
    return toSavePatrimoines;
  }

  public List<Patrimoine> getAllPatrimoine(PageRequest pageRequest) {
    List<File> patrimoineFiles = new ArrayList<>();
    var lsRequest =
        ListObjectsV2Request.builder()
            .bucket(bucketComponent.getBucketName())
            .prefix(PATRIMOINE_KEY_PREFIX)
            .maxKeys(pageRequest.pageSize())
            .build();

    ListObjectsV2Iterable lsIterable = bucketConf.getS3Client().listObjectsV2Paginator(lsRequest);

    int pageNumber = 1;
    for (ListObjectsV2Response objects : lsIterable) {
      if (pageRequest.pageNumber() == pageNumber) {
        objects
            .contents()
            .forEach(
                    file -> patrimoineFiles.add(bucketComponent.download(file.key())));
      }
      pageNumber++;
    }

    return patrimoineFiles.stream()
        .map(
            file -> {
              try {
                return serializer.deserialise(Files.readString(file.toPath()));
              } catch (IOException e) {
                throw new InternalServerErrorException("Cannot read " + file.getName());
              }
            })
        .toList();
  }

  private void writeFile(Path parentDir, Patrimoine patrimoine) {
    try {
      Path filePath = createFile(parentDir.resolve(StringNormalizer.apply(patrimoine.nom())));
      Files.writeString(filePath, serializer.serialise(patrimoine));
    } catch (IOException e) {
      throw new InternalServerErrorException("Unable to save all objects");
    }
  }
}
