import { mixins } from 'vue-class-component';
import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import JhiDataUtils from "@/shared/data/data-utils.service";
import AccountService from "@/account/account.service";
import {IPatient} from "@/shared/model/patient.model";
import {Detection, IDetection} from "@/shared/model/detection.model";
import DetectionService from "@/entities/detection/detection.service";
import {IVisite, Visite} from "@/shared/model/visite.model";
import VisiteService from "@/entities/visite/visite.service";
import {IMaladie, Maladie} from "@/shared/model/maladie.model";
import MaladieService from "@/entities/maladie/maladie.service";
import {IStade} from "@/shared/model/stade.model";
import StadeService from "@/entities/stade/stade.service";
import PatientService from "@/entities/patient/patient.service";

const validations: any = {
  detection: {
    photo: {},
    validation: {},
    stade: {},
  },
};

@Component({
  mixins: [Vue2Filters.mixin],
  validations

})
export default class MedecinPatients extends mixins(JhiDataUtils) {
  @Inject('accountService') private accountService: () => AccountService;
  @Inject('detectionService') private detectionService: () => DetectionService;
  @Inject('visiteService') private visiteService: () => VisiteService;
  @Inject('maladieService') private maladieService: () => MaladieService;
  @Inject('stadeService') private stadeService: () => StadeService;
  @Inject('patientService') private patientService: () => PatientService;

  public patients: IPatient[] = [];
  public patient: IPatient;
  private detection: IDetection = new Detection();
  private visites: IVisite[] = [];
  private maladies: IMaladie[] = [];
  private stades: IStade[] = [];
  private maladie: IMaladie = new Maladie();
  private visite: IVisite;
  private idVisite: number = null;
  private idMaladie: number = null;

  public mounted(): void {
    this.retrieveAllPatients();
    this.retrieveAllVisites();
    this.retrieveAllMaladies();
    this.retrieveAllStades();
  }

  public async retrieveAllStades() {
    try{
      let stades = await this.stadeService().retrieve();
      this.stades = stades.data;
    }catch (e){
      console.log(e);
    }
  }

  public async retrieveAllPatients() {
    try{
      let patients = await this.accountService().retrievePatients();
      this.patients = patients.data;
      if(this.patients == null || this.patients.length == 0){
        this.$root.$bvToast.toast("Vous n'avez aucun patient", {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'info',
          solid: true,
          autoHideDelay: 5000,
        });
        this.$router.push('/rendez-vous');
      }
    }catch (e){
      console.log(e);
    }
  }

  public async retrieveAllVisites() {
    let user = JSON.parse(sessionStorage.getItem('user-info'));
    let rdvs = user.medecin.rendezVous;
    for(let i=0 ; i< rdvs.length; i++){
      if(rdvs[i].visite){
        rdvs[i].visite.rendezVous = rdvs[i];
        this.visites.push(rdvs[i].visite);
      }
    }
  }

  public async retrieveAllMaladies() {
    try {
      let response = await this.maladieService().retrieve();
      this.maladies = response.data;
    }catch (e) {
      console.log(e)
    }
  }

  public async prepareDetection(instance: IPatient){
    this.patient = instance;
    let visites = await this.visiteService().retrieveForMed(instance.id);
    this.visites = visites.data;
    if (<any>this.$refs.detectionEntity) {
      (<any>this.$refs.detectionEntity).show();
    }
  }

  public prepareStade(instance: IPatient): void{
    this.patient = instance;

  }

  public async saveDetection(){
    this.detection.date = new Date();
    this.detection.patient = this.patient;
    this.maladie = await this.maladieService().find(this.idMaladie);
    this.detection.maladie = this.maladie;
    const response = await this.detectionService().create(this.detection);
    this.detection = response;

    this.$root.$bvToast.toast("A detection is created", {
      toaster: 'b-toaster-top-center',
      title: 'Info',
      variant: 'info',
      solid: true,
      autoHideDelay: 5000,
    });
    this.closeDialog();
    if (<any>this.$refs.afficheEntity) {
      (<any>this.$refs.afficheEntity).show();
    }
    this.visite = await this.visiteService().find(this.idVisite);
    this.visite.detection = this.detection;
   await this.visiteService().update(this.visite);
  }

  public async saveStade(instance: IStade){
    this.patient.stade = instance;
    try {
      await this.patientService().update(this.patient);
      this.$root.$bvToast.toast("A Stade and Patient are affected", {
        toaster: 'b-toaster-top-center',
        title: 'Info',
        variant: 'info',
        solid: true,
        autoHideDelay: 5000,
      });
      this.closeDialog();
    }catch (e) {
      console.log(e);
    }

  }

  public closeDialog(): void {
    (<any>this.$refs.detectionEntity).hide();
    (<any>this.$refs.afficheEntity).hide();
    (<any>this.$refs.stadeEntity).hide();
  }

}
