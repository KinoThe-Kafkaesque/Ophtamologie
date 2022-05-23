<template>
  <b-navbar data-cy="navbar" toggleable="md" type="dark">
    <b-navbar-brand class="logo" b-link to="/">
      <span class="navbar-title">HealthCare</span>
    </b-navbar-brand>

    <b-collapse is-nav id="header-tabs">
      <b-navbar-nav class="ml-auto">
        <b-nav-item to="/" exact>Home</b-nav-item>
        <b-nav-item to="/admin/user-management" v-if="(hasAnyAuthority('ROLE_ADMIN') || hasAnyAuthority('SECRETAIRE')) && authenticated">Comptes</b-nav-item>
        <b-nav-item to="/medecin" v-if="hasAnyAuthority('ROLE_ADMIN') && authenticated">Medecins</b-nav-item>
        <b-nav-item to="/patient/medecins" v-if="hasAnyAuthority('PATIENT') && authenticated">Mes medecins</b-nav-item>
        <b-nav-item to="/medecin/patients" v-if="hasAnyAuthority('MEDECIN') && authenticated">Mes patients</b-nav-item>
        <b-nav-item to="/patient" v-if="hasAnyAuthority('SECRETAIRE') && authenticated">Patients</b-nav-item>
        <b-nav-item to="/secretaire" v-if="hasAnyAuthority('ROLE_ADMIN') && authenticated">Secretaires</b-nav-item>
        <b-nav-item to="/rendez-vous" v-if="(hasAnyAuthority('PATIENT') || hasAnyAuthority('SECRETAIRE') || hasAnyAuthority('MEDECIN')) && authenticated">Rendez-vous</b-nav-item>
        <b-nav-item to="/visite" v-if="(hasAnyAuthority('PATIENT') || hasAnyAuthority('SECRETAIRE')|| hasAnyAuthority('MEDECIN')) && authenticated">Visites</b-nav-item>
        <b-nav-item to="/detection" v-if="(hasAnyAuthority('MEDECIN') || hasAnyAuthority('PATIENT'))&& authenticated">Detections</b-nav-item>
        <b-nav-item to="/maladie" v-if="(hasAnyAuthority('MEDECIN') || hasAnyAuthority('PATIENT')) && authenticated">Maladies</b-nav-item>
        <b-nav-item v-on:click="openLogin()" v-if="!authenticated">Sign in</b-nav-item>
        <b-nav-item to="/register" v-if="!authenticated">Register</b-nav-item>

        <b-nav-item href="javascript:void(0);" :class="{ 'router-link-active': subIsActive('/account') }" v-on:click="logout()" v-if="authenticated">Sign out</b-nav-item>
      </b-navbar-nav>
    </b-collapse>
  </b-navbar>
</template>

<script lang="ts" src="./jhi-navbar.component.ts"></script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
/* ==========================================================================
    Navbar
    ========================================================================== */
.navbar-version {
  font-size: 10px;
}

.navbar {
  font-size: 0.95rem;
  text-transform: none;
  padding: 0.7rem 1rem;
  background-color: #106eea;
}

span.navbar-title {
  font-size: 1.4rem;
}

@media screen and (min-width: 768px) {
  .jh-navbar-toggler {
    display: none;
  }
}

@media screen and (min-width: 768px) and (max-width: 1150px) {
  span span {
    display: none;
  }
}

.navbar-title {
  display: inline-block;
  vertical-align: middle;
}

/* ==========================================================================
    Logo styles
    ========================================================================== */
.navbar-brand.logo {
  padding: 5px 15px;
}

.logo .logo-img {
  height: 45px;
  display: inline-block;
  vertical-align: middle;
  width: 70px;
}

.logo-img {
  height: 100%;
  background: url('../../../content/images/logo-jhipster.png') no-repeat center center;
  background-size: contain;
  width: 100%;
  filter: drop-shadow(0 0 0.05rem white);
}
</style>
