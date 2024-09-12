package com.harena.api.endpoint.rest.mapper;

import static com.harena.api.endpoint.rest.model.Possession.TypeEnum.*;
import static java.util.Objects.requireNonNull;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.patrimoine.modele.possession.*;

@Component
@RequiredArgsConstructor
public class PossesionMapper
    implements Mapper<Possession, com.harena.api.endpoint.rest.model.Possession> {
  private final FluxAgentMapper fluxAgentMapper;
  private final MaterielMapper materielMapper;
  private final ArgentMapper argentMapper;
  private final AchatMaterielAuComptantMapper achatMaterielAuComptantMapper;
  private final TransfertArgentMapper transfertArgentMapper;

  @Override
  public com.harena.api.endpoint.rest.model.Possession toRestModel(Possession objectModel) {
    var possession = new com.harena.api.endpoint.rest.model.Possession();
    if (objectModel instanceof FluxArgent fluxArgent) {
      possession.setType(FLUX_ARGENT);
      var flux = fluxAgentMapper.toRestModel(fluxArgent);
      possession.setFluxArgent(flux);
      return possession;
    } else if (objectModel instanceof Materiel materiel) {
      possession.setType(MATERIEL);
      var material = materielMapper.toRestModel(materiel);
      possession.setMateriel(material);
    } else if (objectModel instanceof Argent argent) {
      possession.setType(ARGENT);
      var money = argentMapper.toRestModel(argent);
      possession.setArgent(money);
    } else if (objectModel instanceof AchatMaterielAuComptant achatMaterielAuComptant) {
      possession.setType(ACHAT_MATERIEL_AU_COMPTANT);
      var material = achatMaterielAuComptantMapper.toRestModel(achatMaterielAuComptant);
      possession.setAchatMaterielAuComptant(material);
    } else if (objectModel instanceof TransfertArgent transfertArgent) {
      possession.setType(TRANSFERT_ARGENT);
      var money = transfertArgentMapper.toRestModel(transfertArgent);
      possession.setTransfertArgent(money);
    }
    return possession;
  }

  @Override
  public Possession toObjectModel(com.harena.api.endpoint.rest.model.Possession restModel) {
    return switch (requireNonNull(restModel.getType())) {
      case FLUX_ARGENT -> fluxAgentMapper.toObjectModel(requireNonNull(restModel.getFluxArgent()));
      case MATERIEL -> materielMapper.toObjectModel(requireNonNull(restModel.getMateriel()));
      case ACHAT_MATERIEL_AU_COMPTANT -> achatMaterielAuComptantMapper.toObjectModel(
          requireNonNull(restModel.getAchatMaterielAuComptant()));
      case TRANSFERT_ARGENT -> transfertArgentMapper.toObjectModel(
          requireNonNull(restModel.getTransfertArgent()));
      default -> argentMapper.toObjectModel(requireNonNull(restModel.getArgent()));
    };
  }
}
