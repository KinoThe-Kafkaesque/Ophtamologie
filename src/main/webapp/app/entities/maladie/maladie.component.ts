import {Component, Inject, Vue} from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import {IMaladie} from '@/shared/model/maladie.model';

import MaladieService from './maladie.service';
import AlertService from '@/shared/alert/alert.service';
import StadeService from "@/entities/stade/stade.service";
import {IStade, Stade} from "@/shared/model/stade.model";
import AccountService from "@/account/account.service";

const validations: any = {
  stade: {
    level: {},
    description: {},
  },
};

@Component({
  mixins: [Vue2Filters.mixin],
  validations
})
export default class Maladie extends Vue {
  @Inject('maladieService') private maladieService: () => MaladieService;
  @Inject('alertService') private alertService: () => AlertService;
  @Inject('stadeService') private stadeService: () => StadeService;
  @Inject('accountService') private accountService: () => AccountService;

  private removeId: number = null;

  private affecteId: number = null;

  public maladies: IMaladie[] = [];
  public maladie: IMaladie;

  private stade: IStade = new Stade();

  public isFetching = false;

  public mounted(): void {
    if(this.isMedecin())
      this.retrieveAllMaladies();
    else
      this.retrieveAllMaladiesForPatient();
  }

  public clear(): void {
    this.retrieveAllMaladies();
  }

  public retrieveAllMaladies(): void {
    this.isFetching = true;
    this.maladieService()
      .retrieve()
      .then(
        res => {
          this.maladies = res.data;
          this.isFetching = false;
        },
        err => {
          this.isFetching = false;
          this.alertService().showHttpError(this, err.response);
        }
      );
    this.isFetching = false;
  }

  public retrieveAllMaladiesForPatient(): void {
    this.isFetching = true;
    let user = JSON.parse(sessionStorage.getItem('user-info'));
    this.maladies.push(user.patient.stade.maladie);
    this.isFetching = false;
  }
  public handleSyncList(): void {
    this.clear();
  }

  public prepareRemove(instance: IMaladie): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  // preparation de l'affectation
  public prepareAffecte(instance: IMaladie): void{
    this.maladie = instance;
  }

  public async saveStade(){
    this.stade.maladie = this.maladie;
    try {
      await this.stadeService().create(this.stade);
      this.$bvToast.toast('A stade is created', {
        toaster: 'b-toaster-top-center',
        title: 'Sucess',
        variant: 'success',
        solid: true,
        autoHideDelay: 5000,
      });
      this.closeDialog();
      this.stade = new Stade();
    }catch (e) {
      console.log(e);
    }

  }

  public removeMaladie(): void {
    this.maladieService()
      .delete(this.removeId)
      .then(() => {
        const message = 'A Maladie is deleted';
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Success',
          variant: 'success',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllMaladies();
        this.closeDialog();
      })
      .catch(error => {
        this.$bvToast.toast("You can't delete this Maladie", {
          toaster: 'b-toaster-top-center',
          title: 'Error',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
      });
  }

  public isMedecin(): boolean {
    return this.accountService().userAuthorities.includes('MEDECIN');
  }

  public closeDialog(): void {
    (<any>this.$refs.removeEntity).hide();
    (<any>this.$refs.affecteEntity).hide();
  }
}
