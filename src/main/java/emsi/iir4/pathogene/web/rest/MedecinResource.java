package emsi.iir4.pathogene.web.rest;

import emsi.iir4.pathogene.domain.Medecin;
import emsi.iir4.pathogene.repository.MedecinRepository;
import emsi.iir4.pathogene.security.AuthoritiesConstants;
import emsi.iir4.pathogene.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link emsi.iir4.pathogene.domain.Medecin}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class MedecinResource {

    private final Logger log = LoggerFactory.getLogger(MedecinResource.class);

    private static final String ENTITY_NAME = "medecin";

    private AccountResource accountResource;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MedecinRepository medecinRepository;

    public MedecinResource(MedecinRepository medecinRepository, AccountResource accountResource) {
        this.medecinRepository = medecinRepository;
        this.accountResource = accountResource;
    }

    /**
     * {@code POST  /medecins} : Create a new medecin.
     *
     * @param medecin the medecin to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new medecin, or with status {@code 400 (Bad Request)} if the medecin has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/medecins")
    public ResponseEntity<Medecin> createMedecin(@Valid @RequestBody Medecin medecin) throws URISyntaxException {
        log.debug("REST request to save Medecin : {}", medecin);
        if (medecin.getId() != null) {
            throw new BadRequestAlertException("A new medecin cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Medecin result = medecinRepository.save(medecin);
        return ResponseEntity
            .created(new URI("/api/medecins/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /medecins/:id} : Updates an existing medecin.
     *
     * @param id the id of the medecin to save.
     * @param medecin the medecin to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated medecin,
     * or with status {@code 400 (Bad Request)} if the medecin is not valid,
     * or with status {@code 500 (Internal Server Error)} if the medecin couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/medecins/{id}")
    public ResponseEntity<Medecin> updateMedecin(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Medecin medecin
    ) throws URISyntaxException {
        log.debug("REST request to update Medecin : {}, {}", id, medecin);
        if (medecin.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, medecin.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!medecinRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Medecin result = medecinRepository.save(medecin);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, medecin.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /medecins/:id} : Partial updates given fields of an existing medecin, field will ignore if it is null
     *
     * @param id the id of the medecin to save.
     * @param medecin the medecin to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated medecin,
     * or with status {@code 400 (Bad Request)} if the medecin is not valid,
     * or with status {@code 404 (Not Found)} if the medecin is not found,
     * or with status {@code 500 (Internal Server Error)} if the medecin couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/medecins/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Medecin> partialUpdateMedecin(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Medecin medecin
    ) throws URISyntaxException {
        log.debug("REST request to partial update Medecin partially : {}, {}", id, medecin);
        if (medecin.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, medecin.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!medecinRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Medecin> result = medecinRepository
            .findById(medecin.getId())
            .map(existingMedecin -> {
                if (medecin.getCode() != null) {
                    existingMedecin.setCode(medecin.getCode());
                }
                if (medecin.getNom() != null) {
                    existingMedecin.setNom(medecin.getNom());
                }
                if (medecin.getNumEmp() != null) {
                    existingMedecin.setNumEmp(medecin.getNumEmp());
                }
                if (medecin.getPrenom() != null) {
                    existingMedecin.setPrenom(medecin.getPrenom());
                }
                if (medecin.getExpertLevel() != null) {
                    existingMedecin.setExpertLevel(medecin.getExpertLevel());
                }
                if (medecin.getPhoto() != null) {
                    existingMedecin.setPhoto(medecin.getPhoto());
                }
                if (medecin.getPhotoContentType() != null) {
                    existingMedecin.setPhotoContentType(medecin.getPhotoContentType());
                }
                if (medecin.getType() != null) {
                    existingMedecin.setType(medecin.getType());
                }
                if (medecin.getNbrPatients() != null) {
                    existingMedecin.setNbrPatients(medecin.getNbrPatients());
                }
                if (medecin.getRating() != null) {
                    existingMedecin.setRating(medecin.getRating());
                }
                if (medecin.getDescription() != null) {
                    existingMedecin.setDescription(medecin.getDescription());
                }

                return existingMedecin;
            })
            .map(medecinRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, medecin.getId().toString())
        );
    }

    /**
     * {@code GET  /medecins} : get all the medecins.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of medecins in body.
     */
    @GetMapping("/medecins")
    public List<Medecin> getAllMedecins() {
        log.debug("REST request to get all Medecins");
        List<Medecin> medecins;
        if (accountResource.getAccount().getAuthorities().contains(AuthoritiesConstants.SECRETAIRE)) {
            medecins =
                medecinRepository
                    .findAll()
                    .stream()
                    .filter(m -> m.getSecretaire().getId().equals(accountResource.getAccount().getSecretaire().getId()))
                    .collect(Collectors.toList());
        } else {
            medecins = medecinRepository.findAll();
        }
        return medecins;
    }
    /**
     * {@code GET  /medecins/:id} : get the "id" medecin.
     *
     * @param id the id of the medecin to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the medecin, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/medecins/{id}")
    public ResponseEntity<Medecin> getMedecin(@PathVariable Long id) {
        log.debug("REST request to get Medecin : {}", id);
        Optional<Medecin> medecin = medecinRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(medecin);
    }

    /**
     * {@code DELETE  /medecins/:id} : delete the "id" medecin.
     *
     * @param id the id of the medecin to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/medecins/{id}")
    public ResponseEntity<Void> deleteMedecin(@PathVariable Long id) {
        log.debug("REST request to delete Medecin : {}", id);
        medecinRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
