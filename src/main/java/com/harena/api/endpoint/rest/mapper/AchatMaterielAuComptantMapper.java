package com.harena.api.endpoint.rest.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.patrimoine.modele.possession.AchatMaterielAuComptant;

import java.time.LocalDate;

import static java.util.Objects.requireNonNullElse;

@Component
@RequiredArgsConstructor
class AchatMaterielAuComptantMapper implements Mapper<AchatMaterielAuComptant, com.harena.api.endpoint.rest.model.AchatMaterielAuComptant> {
  private final DeviseMapper deviseMapper;
  private final PossesionMapper possesionMapper;

  @Override
  public com.harena.api.endpoint.rest.model.AchatMaterielAuComptant toRestModel(AchatMaterielAuComptant objectModel) {
    return new com.harena.api.endpoint.rest.model.AchatMaterielAuComptant()
        .nom(objectModel.getNom())
        .t(objectModel.getT())
        .valeurComptable(objectModel.getValeurComptable())
        .devise(deviseMapper.toRestModel(objectModel.getDevise()))
            .achatCommeGroupe(possesionMapper.toRestModel(objectModel.projectionFuture(LocalDate.now())));
  }

  @Override
  public AchatMaterielAuComptant toObjectModel(com.harena.api.endpoint.rest.model.AchatMaterielAuComptant restModel) {
    var nom = restModel.getNom();
    var tTime = restModel.getT();
    int valeurComptableALAchat = requireNonNullElse(restModel.getValeurComptable(), 0);
    var restDevise = restModel.getDevise();
    if (restDevise == null) {
      return new AchatMaterielAuComptant(nom, tTime, valeurComptableALAchat, 0., null);
    }
    var devise = new DeviseMapper().toObjectModel(restDevise);
    return new AchatMaterielAuComptant(
        nom, tTime, valeurComptableALAchat, 0., null,  devise);
  }
}
