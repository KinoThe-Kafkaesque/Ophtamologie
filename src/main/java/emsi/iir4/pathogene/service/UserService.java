package emsi.iir4.pathogene.service;

import emsi.iir4.pathogene.config.Constants;
import emsi.iir4.pathogene.domain.Authority;
import emsi.iir4.pathogene.domain.Medecin;
import emsi.iir4.pathogene.domain.Patient;
import emsi.iir4.pathogene.domain.Secretaire;
import emsi.iir4.pathogene.domain.User;
import emsi.iir4.pathogene.repository.AuthorityRepository;
import emsi.iir4.pathogene.repository.MedecinRepository;
import emsi.iir4.pathogene.repository.PatientRepository;
import emsi.iir4.pathogene.repository.SecretaireRepository;
import emsi.iir4.pathogene.repository.UserRepository;
import emsi.iir4.pathogene.security.AuthoritiesConstants;
import emsi.iir4.pathogene.security.SecurityUtils;
import emsi.iir4.pathogene.service.dto.AdminUserDTO;
import emsi.iir4.pathogene.service.dto.UserDTO;
import emsi.iir4.pathogene.web.rest.AccountResource;
import emsi.iir4.pathogene.web.rest.vm.ManagedUserVM;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.security.RandomUtil;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final MedecinRepository medecinRepository;

    private final SecretaireRepository secretaireRepository;

    private final PatientRepository patientRepository;

    private final AuthorityRepository authorityRepository;

    public UserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthorityRepository authorityRepository,
        MedecinRepository medecinRepository,
        SecretaireRepository secretaireRepository,
        PatientRepository patientRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.medecinRepository = medecinRepository;
        this.secretaireRepository = secretaireRepository;
        this.patientRepository = patientRepository;
    }

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository
            .findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                log.debug("Activated user: {}", user);
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository
            .findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minus(1, ChronoUnit.DAYS)))
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                return user;
            });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository
            .findOneByEmailIgnoreCase(mail)
            .filter(User::isActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                return user;
            });
    }

    public User registerUser(AdminUserDTO userDTO, String password) {
        userRepository
            .findOneByLogin(userDTO.getLogin().toLowerCase())
            .ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new UsernameAlreadyUsedException();
                }
            });
        userRepository
            .findOneByEmailIgnoreCase(userDTO.getEmail())
            .ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new EmailAlreadyUsedException();
                }
            });
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(userDTO.getLogin().toLowerCase());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            newUser.setEmail(userDTO.getEmail().toLowerCase());
        }
        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangKey(userDTO.getLangKey());
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.PATIENT).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        return true;
    }

    public User createUser(ManagedUserVM userDTO) {
        User user = new User();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        /*  normally, the password randomly generated by the application then encrypted
         but I changed so it takes the password from the userDTO instead of randomly generated one
         I kept the previous code for failsafe
        */
        String encryptedPassword;

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty() && !userDTO.getPassword().equals("")) {
            encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        } else {
            encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        }
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO
                .getAuthorities()
                .stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }
        createBasedOnRole(userDTO, user);
        userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    public User createAdministeredUser(ManagedUserVM userDTO) {
        User user = new User();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        /*  normally, the password randomly generated by the application then encrypted
         but I changed so it takes the password from the userDTO instead of randomly generated one
         I kept the previous code for failsafe
        */
        String encryptedPassword;

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty() && !userDTO.getPassword().equals("")) {
            encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        } else {
            encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        }
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO
                .getAuthorities()
                .stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }
        userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    public void createBasedOnRole(ManagedUserVM userDTO, User user) {
        // now we take the authorities and create profiles based on the authorities
        // btw thanks to Copilot for generating the code :3 really cool
        userDTO
            .getAuthorities()
            .forEach(authority -> {
                if (authority.equals(AuthoritiesConstants.MEDECIN)) {
                    Medecin medecin = new Medecin();
                    medecin.user(user);
                    medecinRepository.save(medecin);
                } else if (authority.equals(AuthoritiesConstants.SECRETAIRE)) {
                    Secretaire secretaire = new Secretaire();
                    secretaire.user(user);
                    secretaireRepository.save(secretaire);
                } else if (authority.equals(AuthoritiesConstants.PATIENT)) {
                    Patient patient = new Patient();
                    patient.user(user);
                    patientRepository.save(patient);
                }
            });
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<AdminUserDTO> updateUser(AdminUserDTO userDTO) {
        return Optional
            .of(userRepository.findById(userDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(user -> {
                user.setLogin(userDTO.getLogin().toLowerCase());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                if (userDTO.getEmail() != null) {
                    user.setEmail(userDTO.getEmail().toLowerCase());
                }
                user.setImageUrl(userDTO.getImageUrl());
                user.setActivated(userDTO.isActivated());
                user.setLangKey(userDTO.getLangKey());
                Set<Authority> managedAuthorities = user.getAuthorities();
                managedAuthorities.clear();
                userDTO
                    .getAuthorities()
                    .stream()
                    .map(authorityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(managedAuthorities::add);
                log.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(AdminUserDTO::new);
    }

    public void deleteUser(String login) {
        //BuisnessLogic.deleteUser(login);

        //I detach the Profiles related to the user to avoid the cascade delete of the user
        //PS : I got overwhelemed and I didn't read the docs for CASCADE.DETACH
        //I'm not sure if it's the best way to do it.
        //Wink wink

        Optional<Medecin> medecin;
        Optional<Patient> patient;
        Optional<Secretaire> secretaire;
        if ((medecin = medecinRepository.findByUserLogin(login)).isPresent()) {
            medecin.get().setUser(null);
            medecinRepository.save(medecin.get());
        }
        if ((patient = patientRepository.findByUserLogin(login)).isPresent()) {
            patient.get().setUser(null);
            patientRepository.save(patient.get());
        }
        if ((secretaire = secretaireRepository.findByUserLogin(login)).isPresent()) {
            secretaire.get().setUser(null);
            secretaireRepository.save(secretaire.get());
        }

        userRepository
            .findOneByLogin(login)
            .ifPresent(user -> {
                userRepository.delete(user);
                log.debug("Deleted User: {}", user);
            });
    }

    /**
     * Update basic information (first name, last name, email, language) for the
     * current user.
     *
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     * @param langKey   language key.
     * @param imageUrl  image URL of user.
     */

    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
        SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                if (email != null) {
                    user.setEmail(email.toLowerCase());
                }
                user.setLangKey(langKey);
                user.setImageUrl(imageUrl);
                log.debug("Changed Information for User: {}", user);
            });
    }

    @Transactional
    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw new InvalidPasswordException();
                }
                String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                log.debug("Changed password for User: {}", user);
            });
    }

    @Transactional(readOnly = true)
    public Page<AdminUserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(AdminUserDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<AdminUserDTO> getAllSecPatients(Pageable pageable, Long id) {
        Set<Patient> patients = patientRepository.findBySecretaireId(id);
        // collect users from all patients
        Set<User> users = new HashSet<>();
        patients.forEach(patient -> users.add(patient.getUser()));
        // transform users to PAGE using pageable
        return new PageImpl<AdminUserDTO>(users.stream().map(AdminUserDTO::new).collect(Collectors.toList()), pageable, users.size());
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllPublicUsers(Pageable pageable) {
        return userRepository.findAllByIdNotNullAndActivatedIsTrue(pageable).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
            .forEach(user -> {
                log.debug("Deleting not activated user {}", user.getLogin());
                userRepository.delete(user);
            });
    }

    /**
     * Gets a list of all the authorities.
     *
     * @return a list of all the authorities.
     */
    @Transactional(readOnly = true)
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }
}
