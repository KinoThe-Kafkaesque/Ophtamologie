import { Component, Inject } from 'vue-property-decorator';

import { mixins } from 'vue-class-component';
import JhiDataUtils from '@/shared/data/data-utils.service';

import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';

import RendezVousService from '@/entities/rendez-vous/rendez-vous.service';
import { IRendezVous } from '@/shared/model/rendez-vous.model';
import MaladieService from '@/entities/maladie/maladie.service';
import { IMaladie } from '@/shared/model/maladie.model';
import DetectionService from '@/entities/detection/detection.service';
import { IDetection } from '@/shared/model/detection.model';

import SecretaireService from '@/entities/secretaire/secretaire.service';
import { ISecretaire } from '@/shared/model/secretaire.model';

import StadeService from '@/entities/stade/stade.service';
import { IStade } from '@/shared/model/stade.model';

import { IPatient, Patient } from '@/shared/model/patient.model';
import PatientService from './patient.service';
import { Genre } from '@/shared/model/enumerations/genre.model';

const validations: any = {
  patient: {
    code: {},
    nom: {},
    prenom: {},
    dateNaissance: {},
    adresse: {},
    genre: {},
    telephone: {},
    poids: {},
    taille: {},
    photo: {},
  },
};

@Component({
  validations,
})
export default class PatientUpdate extends mixins(JhiDataUtils) {
  @Inject('patientService') private patientService: () => PatientService;
  @Inject('alertService') private alertService: () => AlertService;

  public patient: IPatient = new Patient();

  @Inject('userService') private userService: () => UserService;

  public users: Array<any> = [];

  @Inject('rendezVousService') private rendezVousService: () => RendezVousService;

  public rendezVous: IRendezVous[] = [];
  @Inject('maladieService') private maladieService: () => MaladieService;

  public maladies: IMaladie[] = [];

  @Inject('detectionService') private detectionService: () => DetectionService;

  public detections: IDetection[] = [];

  @Inject('secretaireService') private secretaireService: () => SecretaireService;

  public secretaires: ISecretaire[] = [];

  @Inject('stadeService') private stadeService: () => StadeService;

  public stades: IStade[] = [];
  public genreValues: string[] = Object.keys(Genre);
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.patientId) {
        vm.retrievePatient(to.params.patientId);
      }
      vm.initRelationships();
    });
  }

  created(): void {
    this.currentLanguage = this.$store.getters.currentLanguage;
    this.$store.watch(
      () => this.$store.getters.currentLanguage,
      () => {
        this.currentLanguage = this.$store.getters.currentLanguage;
      }
    );
    this.patient.detections = [];
  }

  public save(): void {
    this.isSaving = true;
    if (this.patient.id) {
      this.patientService()
        .update(this.patient)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Patient is updated';
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    } else {
      const user = JSON.parse(sessionStorage.getItem('user-info'));
      this.patient.secretaire = user;
      this.patientService()
        .create(this.patient)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Patient is created';
          this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Success',
            variant: 'success',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    }
  }

  public retrievePatient(patientId): void {
    this.patientService()
      .find(patientId)
      .then(res => {
        this.patient = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public clearInputImage(field, fieldContentType, idInput): void {
    if (this.patient && field && fieldContentType) {
      if (Object.prototype.hasOwnProperty.call(this.patient, field)) {
        this.patient[field] = null;
      }
      if (Object.prototype.hasOwnProperty.call(this.patient, fieldContentType)) {
        this.patient[fieldContentType] = null;
      }
      if (idInput) {
        (<any>this).$refs[idInput] = null;
      }
    }
  }

  public initRelationships(): void {
    this.userService()
      .retrieve()
      .then(res => {
        this.users = res.data;
      });
    this.rendezVousService()
      .retrieve()
      .then(res => {
        this.rendezVous = res.data;
      });
    this.detectionService()
      .retrieve()
      .then(res => {
        this.detections = res.data;
      });
    this.secretaireService()
      .retrieve()
      .then(res => {
        this.secretaires = res.data;
      });
    this.stadeService()
      .retrieve()
      .then(res => {
        this.stades = res.data;
      });
  }

  public getSelected(selectedVals, option): any {
    if (selectedVals) {
      return selectedVals.find(value => option.id === value.id) ?? option;
    }
    return option;
  }
}
