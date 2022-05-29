package emsi.iir4.pathogene.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import emsi.iir4.pathogene.domain.enumeration.Genre;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Patient.
 */
@Entity
@Table(name = "patient")
public class Patient implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "nom")
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "adresse")
    private String adresse;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre")
    private Genre genre;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "poids")
    private Double poids;

    @Column(name = "taille")
    private Double taille;

    @Lob
    @Column(name = "photo")
    @Basic(fetch = FetchType.LAZY)
    private byte[] photo;

    @Column(name = "photo_content_type")
    private String photoContentType;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;


    @OneToMany(mappedBy = "patient", fetch = FetchType.EAGER)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<RendezVous> rendezVous = new HashSet<>();

    @ManyToMany( fetch = FetchType.EAGER)
    @JoinTable(
        name = "rel_patient__detection",
        joinColumns = @JoinColumn(name = "patient_id"),
        inverseJoinColumns = @JoinColumn(name = "detection_id")
    )
    @JsonIgnoreProperties(value = { "patients" }, allowSetters = true)
    private Set<Detection> detections = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "medecins", "patients" }, allowSetters = true)
    private Secretaire secretaire;


    @ManyToOne
    @JsonIgnoreProperties(value = { "patients", "classifications", "images" }, allowSetters = true)
    private Stade stade;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Patient id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Patient code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return this.nom;
    }

    public Patient nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public Patient prenom(String prenom) {
        this.setPrenom(prenom);
        return this;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return this.dateNaissance;
    }

    public Patient dateNaissance(LocalDate dateNaissance) {
        this.setDateNaissance(dateNaissance);
        return this;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getAdresse() {
        return this.adresse;
    }

    public Patient adresse(String adresse) {
        this.setAdresse(adresse);
        return this;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Genre getGenre() {
        return this.genre;
    }

    public Patient genre(Genre genre) {
        this.setGenre(genre);
        return this;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public Patient telephone(String telephone) {
        this.setTelephone(telephone);
        return this;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Double getPoids() {
        return this.poids;
    }

    public Patient poids(Double poids) {
        this.setPoids(poids);
        return this;
    }

    public void setPoids(Double poids) {
        this.poids = poids;
    }

    public Double getTaille() {
        return this.taille;
    }

    public Patient taille(Double taille) {
        this.setTaille(taille);
        return this;
    }

    public void setTaille(Double taille) {
        this.taille = taille;
    }

    public byte[] getPhoto() {
        return this.photo;
    }

    public Patient photo(byte[] photo) {
        this.setPhoto(photo);
        return this;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return this.photoContentType;
    }

    public Patient photoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
        return this;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Patient user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<RendezVous> getRendezVous() {
        return this.rendezVous;
    }

    public void setRendezVous(Set<RendezVous> rendezVous) {
        if (this.rendezVous != null) {
            this.rendezVous.forEach(i -> i.setPatient(null));
        }
        if (rendezVous != null) {
            rendezVous.forEach(i -> i.setPatient(this));
        }
        this.rendezVous = rendezVous;
    }

    public Patient rendezVous(Set<RendezVous> rendezVous) {
        this.setRendezVous(rendezVous);
        return this;
    }

    public Patient addRendezVous(RendezVous rendezVous) {
        this.rendezVous.add(rendezVous);
        rendezVous.setPatient(this);
        return this;
    }

    public Patient removeRendezVous(RendezVous rendezVous) {
        this.rendezVous.remove(rendezVous);
        rendezVous.setPatient(null);
        return this;
    }

    public Set<Detection> getDetections() {
        return this.detections;
    }

    public void setDetections(Set<Detection> detections) {
        this.detections = detections;
    }

    public Patient detections(Set<Detection> detections) {
        this.setDetections(detections);
        return this;
    }

    public Patient addDetection(Detection detection) {
        this.detections.add(detection);
        detection.getPatients().add(this);
        return this;
    }

    public Patient removeDetection(Detection detection) {
        this.detections.remove(detection);
        detection.getPatients().remove(this);
        return this;
    }

    public Secretaire getSecretaire() {
        return this.secretaire;
    }

    public void setSecretaire(Secretaire secretaire) {
        this.secretaire = secretaire;
    }

    public Patient secretaire(Secretaire secretaire) {
        this.setSecretaire(secretaire);
        return this;
    }

    public Stade getStade() {
        return this.stade;
    }

    public void setStade(Stade stade) {
        this.stade = stade;
    }

    public Patient stade(Stade stade) {
        this.setStade(stade);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Patient)) {
            return false;
        }
        return id != null && id.equals(((Patient) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Patient{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", dateNaissance='" + getDateNaissance() + "'" +
            ", adresse='" + getAdresse() + "'" +
            ", genre='" + getGenre() + "'" +
            ", telephone='" + getTelephone() + "'" +
            ", poids=" + getPoids() +
            ", taille=" + getTaille() +
            ", photo='" + getPhoto() + "'" +
            ", photoContentType='" + getPhotoContentType() + "'" +
            "}";
    }
}
