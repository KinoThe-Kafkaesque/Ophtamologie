import { Component, Inject } from 'vue-property-decorator';

import { mixins } from 'vue-class-component';
import JhiDataUtils from '@/shared/data/data-utils.service';

import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT } from '@/shared/date/filters';

import AlertService from '@/shared/alert/alert.service';

import VisiteService from '@/entities/visite/visite.service';
import { IVisite } from '@/shared/model/visite.model';

import MaladieService from '@/entities/maladie/maladie.service';
import { IMaladie } from '@/shared/model/maladie.model';

import PatientService from '@/entities/patient/patient.service';
import { IPatient } from '@/shared/model/patient.model';

import { IDetection, Detection } from '@/shared/model/detection.model';
import DetectionService from './detection.service';

const validations: any = {
  detection: {
    photo: {},
    code: {},
    validation: {},
    stade: {},
    date: {},
    description: {},
  },
};

@Component({
  validations,
})
export default class DetectionUpdate extends mixins(JhiDataUtils) {
  @Inject('detectionService') private detectionService: () => DetectionService;
  @Inject('alertService') private alertService: () => AlertService;

  public detection: IDetection = new Detection();

  @Inject('visiteService') private visiteService: () => VisiteService;

  public visites: IVisite[] = [];

  @Inject('maladieService') private maladieService: () => MaladieService;

  public maladies: IMaladie[] = [];

  @Inject('patientService') private patientService: () => PatientService;

  public patients: IPatient[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.detectionId) {
        vm.retrieveDetection(to.params.detectionId);
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
  }

  public save(): void {
    this.isSaving = true;
    if (this.detection.id) {
      this.detectionService()
        .update(this.detection)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Detection is updated with identifier ' + param.id;
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
      this.detection.date = new Date();
      this.detectionService()
        .create(this.detection)
        .then(param => {
          this.isSaving = false;
          this.detection = param;
          if (<any>this.$refs.afficheEntity) {
            (<any>this.$refs.afficheEntity).show();
          }

        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    }
  }

  public convertDateTimeFromServer(date: Date): string {
    if (date && dayjs(date).isValid()) {
      return dayjs(date).format(DATE_TIME_LONG_FORMAT);
    }
    return null;
  }

  public updateInstantField(field, event) {
    if (event.target.value) {
      this.detection[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.detection[field] = null;
    }
  }

  public updateZonedDateTimeField(field, event) {
    if (event.target.value) {
      this.detection[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.detection[field] = null;
    }
  }

  public retrieveDetection(detectionId): void {
    this.detectionService()
      .find(detectionId)
      .then(res => {
        res.date = new Date(res.date);
        this.detection = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public clearInputImage(field, fieldContentType, idInput): void {
    if (this.detection && field && fieldContentType) {
      if (Object.prototype.hasOwnProperty.call(this.detection, field)) {
        this.detection[field] = null;
      }
      if (Object.prototype.hasOwnProperty.call(this.detection, fieldContentType)) {
        this.detection[fieldContentType] = null;
      }
      if (idInput) {
        (<any>this).$refs[idInput] = null;
      }
    }
  }

  public initRelationships(): void {
    this.visiteService()
      .retrieve()
      .then(res => {
        this.visites = res.data;
      });
    this.maladieService()
      .retrieve()
      .then(res => {
        this.maladies = res.data;
      });
    this.patientService()
      .retrieve()
      .then(res => {
        this.patients = res.data;
      });
  }

  public closeDialog(): void {
    (<any>this.$refs.afficheEntity).hide();
  }

}
