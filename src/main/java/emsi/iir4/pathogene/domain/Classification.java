package emsi.iir4.pathogene.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Classification.
 */
@Entity
@Table(name = "classification")
public class Classification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "score")
    private Boolean score;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "classifications", "rendezVous", "secretaire" }, allowSetters = true)
    private Medecin medecin;

    @ManyToOne
    @JsonIgnoreProperties(value = { "classifications", "images", "patients", "maladie" }, allowSetters = true)
    private Stade stade;

    @ManyToOne
    @JsonIgnoreProperties(value = { "classifications", "maladie" }, allowSetters = true)
    private Unclassified unclassified;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Classification id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Classification code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getScore() {
        return this.score;
    }

    public Classification score(Boolean score) {
        this.setScore(score);
        return this;
    }

    public void setScore(Boolean score) {
        this.score = score;
    }

    public Medecin getMedecin() {
        return this.medecin;
    }

    public void setMedecin(Medecin medecin) {
        this.medecin = medecin;
    }

    public Classification medecin(Medecin medecin) {
        this.setMedecin(medecin);
        return this;
    }

    public Stade getStade() {
        return this.stade;
    }

    public void setStade(Stade stade) {
        this.stade = stade;
    }

    public Classification stade(Stade stade) {
        this.setStade(stade);
        return this;
    }

    public Unclassified getUnclassified() {
        return this.unclassified;
    }

    public void setUnclassified(Unclassified unclassified) {
        this.unclassified = unclassified;
    }

    public Classification unclassified(Unclassified unclassified) {
        this.setUnclassified(unclassified);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Classification)) {
            return false;
        }
        return id != null && id.equals(((Classification) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Classification{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", score='" + getScore() + "'" +
            "}";
    }
}
