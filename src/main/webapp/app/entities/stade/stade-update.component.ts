import { Component, Vue, Inject } from 'vue-property-decorator';

import AlertService from '@/shared/alert/alert.service';

import ClassificationService from '@/entities/classification/classification.service';
import { IClassification } from '@/shared/model/classification.model';

import ImageService from '@/entities/image/image.service';
import { IImage } from '@/shared/model/image.model';

import PatientService from '@/entities/patient/patient.service';
import { IPatient } from '@/shared/model/patient.model';

import MaladieService from '@/entities/maladie/maladie.service';
import { IMaladie } from '@/shared/model/maladie.model';

import { IStade, Stade } from '@/shared/model/stade.model';
import StadeService from './stade.service';

const validations: any = {
  stade: {
    code: {},
    level: {},
    description: {},
  },
};

@Component({
  validations,
})
export default class StadeUpdate extends Vue {
  @Inject('stadeService') private stadeService: () => StadeService;
  @Inject('alertService') private alertService: () => AlertService;

  public stade: IStade = new Stade();

  @Inject('classificationService') private classificationService: () => ClassificationService;

  public classifications: IClassification[] = [];

  @Inject('imageService') private imageService: () => ImageService;

  public images: IImage[] = [];

  @Inject('patientService') private patientService: () => PatientService;

  public patients: IPatient[] = [];

  @Inject('maladieService') private maladieService: () => MaladieService;

  public maladies: IMaladie[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.stadeId) {
        vm.retrieveStade(to.params.stadeId);
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
    if (this.stade.id) {
      this.stadeService()
        .update(this.stade)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Stade is updated with identifier ' + param.id;
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
      this.stadeService()
        .create(this.stade)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Stade is created with identifier ' + param.id;
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

  public retrieveStade(stadeId): void {
    this.stadeService()
      .find(stadeId)
      .then(res => {
        this.stade = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.classificationService()
      .retrieve()
      .then(res => {
        this.classifications = res.data;
      });
    this.imageService()
      .retrieve()
      .then(res => {
        this.images = res.data;
      });
    this.patientService()
      .retrieve()
      .then(res => {
        this.patients = res.data;
      });
    this.maladieService()
      .retrieve()
      .then(res => {
        this.maladies = res.data;
      });
  }
}
