import { Component, Vue, Inject } from 'vue-property-decorator';

import { IStade } from '@/shared/model/stade.model';
import StadeService from './stade.service';
import AlertService from '@/shared/alert/alert.service';
import JhiDataUtils from '@/shared/data/data-utils.service';
import { mixins } from 'vue-class-component';
import { IImage } from '@/shared/model/image.model';
import ImageService from "@/entities/image/image.service";

@Component
export default class StadeDetails extends mixins(JhiDataUtils) {
  @Inject('stadeService') private stadeService: () => StadeService;
  @Inject('imageService') private imageService: () => ImageService;
  @Inject('alertService') private alertService: () => AlertService;

  public stade: IStade = {};
  public images: IImage[] = [];
  private removeId: number = null;

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.stadeId) {
        vm.retrieveStade(to.params.stadeId);
      }
    });
  }

  public retrieveStade(stadeId) {
    this.stadeService()
      .find(stadeId)
      .then(res => {
        this.stade = res;
        this.images = res.images;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }
  public prepareRemove(instance: IImage): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }


  public removeImage(): void {
    this.imageService()
      .delete(this.removeId)
      .then(() => {
        const message = 'An Image is deleted';
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.closeDialog();
        this.$router.push('/maladie');
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public closeDialog(): void {
    (<any>this.$refs.removeEntity).hide();
  }
  public previousState() {
    this.$router.go(-1);
  }
}
