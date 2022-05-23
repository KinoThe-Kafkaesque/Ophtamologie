import { Component, Vue, Inject } from 'vue-property-decorator';
import { IMaladie } from '@/shared/model/maladie.model';
import MaladieService from './maladie.service';
import AlertService from '@/shared/alert/alert.service';
import {IStade} from "@/shared/model/stade.model";
import AccountService from "@/account/account.service";
import StadeService from "@/entities/stade/stade.service";
import Vue2Filters from "vue2-filters";
import {mixins} from "vue-class-component";
import JhiDataUtils from "@/shared/data/data-utils.service";
import {IImage, Image} from "@/shared/model/image.model";
import ImageService from "@/entities/image/image.service";

const validations: any = {
  image: {
    photo: {},
  },
};

@Component({
  mixins: [Vue2Filters.mixin],
  validations
})
export default class MaladieDetails extends mixins(JhiDataUtils)  {
  @Inject('maladieService') private maladieService: () => MaladieService;
  @Inject('stadeService') private stadeService: () => StadeService;
  @Inject('alertService') private alertService: () => AlertService;
  @Inject('accountService') private accountService: () => AccountService;
  @Inject('imageService') private imageService: () => ImageService;

  private maladie: IMaladie = {};
  private stades: IStade[] = [];
  private removeId:number = null;
  public stade: IStade;
  private image: IImage = new Image();

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.maladieId) {
        vm.retrieveMaladie(to.params.maladieId);
      }
    });
  }

  public retrieveMaladie(maladieId) {
    this.maladieService()
      .find(maladieId)
      .then(res => {
        this.maladie = res;
        this.stades = this.maladie.stades;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public prepareRemove(instance: IStade): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public removeStade(): void {
    this.stadeService()
      .delete(this.removeId)
      .then(() => {
        const message = 'A Stade is deleted';
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'success',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.closeDialog();
        this.$router.push('/maladie');
      })
      .catch(error => {
        const message = "You can't delete this Stade" ;
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Error',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
      });
  }

  public async saveImage(){
    this.image.stade = this.stade;
    try{
      await this.imageService().create(this.image);
      this.closeDialog();
      return this.$root.$bvToast.toast('An image is created', {
        toaster: 'b-toaster-top-center',
        title: 'Info',
        variant: 'info',
        solid: true,
        autoHideDelay: 5000,
      });
    }catch (e) {
      console.log(e);
    }

  }


  public clearInputImage(field, fieldContentType, idInput): void {
    if (this.image && field && fieldContentType) {
      if (Object.prototype.hasOwnProperty.call(this.image, field)) {
        this.image[field] = null;
      }
      if (Object.prototype.hasOwnProperty.call(this.image, fieldContentType)) {
        this.image[fieldContentType] = null;
      }
      if (idInput) {
        (<any>this).$refs[idInput] = null;
      }
    }
  }

  public prepareCeate(instance: IStade){
    this.stade = instance;
    if (<any>this.$refs.createEntity) {
      (<any>this.$refs.createEntity).show();
    }
  }

  public isMedecin(): boolean {
    return this.accountService().userAuthorities.includes('MEDECIN');
  }

  public previousState() {
    this.$router.go(-1);
  }
  public closeDialog(): void {
    (<any>this.$refs.removeEntity).hide();
    (<any>this.$refs.createEntity).hide();
  }
}
