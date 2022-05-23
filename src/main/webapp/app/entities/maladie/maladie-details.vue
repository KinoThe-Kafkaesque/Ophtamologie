<template>
  <div class="container-fluid">
    <div class="card jh-card">
      <div class="row justify-content-center">
        <div class="col-8">
          <div v-if="maladie">
            <h2 class="jh-entity-heading" data-cy="maladieDetailsHeading"><span>Maladie</span></h2>
            <dl class="row jh-entity-details">
              <dt>
                <span>Code</span>
              </dt>
              <dd>
                <span>{{ maladie.code }}</span>
              </dd>
              <dt>
                <span>Nom</span>
              </dt>
              <dd>
                <span>{{ maladie.nom }}</span>
              </dd>
            </dl>

            <br/>
            <div v-if="stades && stades.length > 0">
              <h2 id="page-heading" data-cy="StadeHeading">
                <span id="stade-heading">Stades</span>
              </h2>
              <hr>
              <div class="table-responsive">
                <table class="table table-striped" aria-describedby="stades">
                  <thead>
                  <tr>
                    <th scope="row"><span>Code</span></th>
                    <th scope="row"><span>Level</span></th>
                    <th scope="row"><span>Description</span></th>
                    <th scope="row" v-if="isMedecin()"></th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr v-for="stade in stades" :key="stade.id" data-cy="entityTable">
                    <td>{{ stade.code }}</td>
                    <td>{{ stade.level }}</td>
                    <td>{{ stade.description }}</td>
                    <td class="text-right"  v-if="isMedecin()">
                      <div class="btn-group">
                      <router-link :to="{ name: 'StadeView', params: { stadeId: stade.id } }" custom v-slot="{ navigate }">
                        <button @click="navigate" class="btn btn-info btn-sm details mr-1" data-cy="entityDetailsButton">
                          <font-awesome-icon icon="eye"></font-awesome-icon>
                          <span class="d-none d-md-inline">View</span>
                        </button>
                      </router-link>
                      <b-button
                        v-on:click="prepareRemove(stade)"
                        variant="danger"
                        class="btn btn-sm mr-1"
                        data-cy="entityDeleteButton"
                        v-b-modal.removeEntity
                      >
                        <font-awesome-icon icon="times"></font-awesome-icon>
                        <span class="d-none d-md-inline">Delete</span>
                      </b-button>
                        <b-button
                          v-on:click="prepareCeate(stade)"
                          variant="success"
                          class="btn btn-sm"
                          data-cy="entityCreateButton"
                          v-b-modal.createEntity
                        >
                          <font-awesome-icon icon="plus"></font-awesome-icon>
                          <span class="d-none d-md-inline">Image</span>
                        </b-button>
                      </div>
                    </td>

                  </tr>
                  </tbody>
                </table>
              </div>
            </div>
            <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info"
                    data-cy="entityDetailsBackButton">
              <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span> Back</span>
            </button>
            <router-link v-if="maladie.id && isMedecin()" :to="{ name: 'MaladieEdit', params: { maladieId: maladie.id } }" custom
                         v-slot="{ navigate }" >
              <button @click="navigate" class="btn btn-primary">
                <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span> Edit</span>
              </button>
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <b-modal ref="removeEntity" id="removeEntity">
      <span slot="modal-title"
      ><span id="pathogeneApp.stade.delete.question" data-cy="stadeDeleteDialogHeading">Confirm delete operation</span></span
      >
      <div class="modal-body">
        <p id="jhi-delete-stade-heading">Are you sure you want to delete this Stade?</p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-stade"
          data-cy="entityConfirmDeleteButton"
          v-on:click="removeStade()"
        >
          Delete
        </button>
      </div>
    </b-modal>
    <b-modal ref="createEntity" id="affecteEntity">
      <span slot="modal-title"
      ><span id="pathogeneApp.maladie.affecte.question"
             data-cy="maladieAffecteDialogHeading">Create an image</span></span
      >
      <div class="modal-body">
        <div>
          <div class="form-group">
            <label class="form-control-label" for="image-photo">Photo</label>
            <div>
              <img
                v-bind:src="'data:' + image.photoContentType + ';base64,' + image.photo"
                style="max-height: 100px"
                v-if="image.photo"
                alt="image image"
              />
              <div v-if="image.photo" class="form-text text-danger clearfix">
                <span class="pull-left">{{ image.photoContentType }}, {{ byteSize(image.photo) }}</span>
                <button
                  type="button"
                  v-on:click="clearInputImage('photo', 'photoContentType', 'file_photo')"
                  class="btn btn-secondary btn-xs pull-right"
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                </button>
              </div>
              <input
                type="file"
                ref="file_photo"
                id="file_photo"
                data-cy="photo"
                v-on:change="setFileData($event, image, 'photo', true)"
                accept="image/*"
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="photo"
              id="image-photo"
              data-cy="photo"
              :class="{ valid: !$v.image.photo.$invalid, invalid: $v.image.photo.$invalid }"
              v-model="$v.image.photo.$model"
            />
            <input
              type="hidden"
              class="form-control"
              name="photoContentType"
              id="image-photoContentType"
              v-model="image.photoContentType"
            />
          </div>
        </div>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Annuler</button>
        <button
          type="submit"
          id="save-entity"
          data-cy="entityCreateSaveButton"
          class="btn btn-primary"
          v-on:click="saveImage()"
        >
          <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>Save</span>
        </button>
      </div>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./maladie-details.component.ts"></script>
