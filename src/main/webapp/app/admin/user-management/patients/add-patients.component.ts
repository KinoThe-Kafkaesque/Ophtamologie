import {Component, Inject} from 'vue-property-decorator';
import {mixins} from 'vue-class-component';
import JhiDataUtils from '@/shared/data/data-utils.service';
import AlertService from '@/shared/alert/alert.service';
import {IPatient, Patient} from '@/shared/model/patient.model';
import {Genre} from '@/shared/model/enumerations/genre.model';
import {IUser, User} from "@/shared/model/user.model";
import UserManagementService from "@/admin/user-management/user-management.service";
import PatientService from "@/entities/patient/patient.service";

const validations: any = {
  patient: {
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
  user: {
    login: {},
    email: {},
    password: {},
  }
};

@Component({
  validations,
})
export default class PatientUpdate extends mixins(JhiDataUtils) {
  @Inject('userManagementService') private userManagementService: () => UserManagementService;
  @Inject('alertService') private alertService: () => AlertService;
  private patientService: PatientService = new PatientService();

  public patient: IPatient = new Patient();
  public genreValues: string[] = Object.keys(Genre);
  public isSaving = false;
  public user: IUser = new User();

  public async save(){
    this.isSaving = true;
    this.user.firstName = this.patient.prenom;
    this.user.lastName = this.patient.nom;
    try {
      let user = JSON.parse(sessionStorage.getItem('user-info'));
      this.patient.secretaire = user.secretaire;
      await this.userManagementService().createPatient({
        user: this.user,
        patient: this.patient
      })
      this.$router.push('/patient');
      this.$root.$bvToast.toast('A Patient is created', {
        toaster: 'b-toaster-top-center',
        title: 'Success',
        variant: 'success',
        solid: true,
        autoHideDelay: 5000,
      });
    }catch (e) {
      console.log(e);
    }

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

}
