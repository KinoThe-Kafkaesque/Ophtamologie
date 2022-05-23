<template>
  <div class="container-fluid">
    <div class="card jh-card">
      <div class="row justify-content-center">
        <div class="col-8">
          <div v-if="stade">
            <h2 class="jh-entity-heading" data-cy="stadeDetailsHeading"><span>Stade</span></h2>
            <dl class="row jh-entity-details">
              <dt>
                <span>Code</span>
              </dt>
              <dd>
                <span>{{ stade.code }}</span>
              </dd>
              <dt>
                <span>Level</span>
              </dt>
              <dd>
                <span>{{ stade.level }}</span>
              </dd>
              <dt>
                <span>Description</span>
              </dt>
              <dd>
                <span>{{ stade.description }}</span>
              </dd>

            </dl>

            <div v-if="images && images.length > 0">
              <br/>
              <h2 id="page-heading" data-cy="ImageHeading">
                <span id="image-heading">Images</span>
              </h2>

              <div class="table-responsive">
                <table class="table table-striped" aria-describedby="stades">
                  <thead>
                  <tr>
                    <th scope="row"><span>Code</span></th>
                    <th scope="row"><span>Photo</span></th>
                    <th scope="row"></th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr v-for="image in images" :key="image.id" data-cy="entityTable">
                    <td>{{ image.code }}</td>
                    <td>
                      <a v-if="image.photo" v-on:click="openFile(image.photoContentType, image.photo)">
                        <img
                          v-bind:src="'data:' + image.photoContentType + ';base64,' + image.photo"
                          style="max-height: 70px"
                          alt="image image"
                        />
                      </a>
                    </td>
                    <td class="text-right">
                      <b-button
                        v-on:click="prepareRemove(image)"
                        variant="danger"
                        class="btn btn-sm"
                        data-cy="entityDeleteButton"
                        v-b-modal.removeEntity
                      >
                        <font-awesome-icon icon="times"></font-awesome-icon>
                        <span class="d-none d-md-inline">Delete</span>
                      </b-button>
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
            <router-link v-if="stade.id" :to="{ name: 'StadeEdit', params: { stadeId: stade.id } }" custom
                         v-slot="{ navigate }">
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
      ><span id="pathogeneApp.image.delete.question" data-cy="imageDeleteDialogHeading">Confirm delete operation</span></span
      >
      <div class="modal-body">
        <p id="jhi-delete-image-heading">Are you sure you want to delete this Image?</p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-image"
          data-cy="entityConfirmDeleteButton"
          v-on:click="removeImage()"
        >
          Delete
        </button>
      </div>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./stade-details.component.ts"></script>
