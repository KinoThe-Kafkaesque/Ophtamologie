package emsi.iir4.pathogene.web.rest;

import static emsi.iir4.pathogene.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import emsi.iir4.pathogene.IntegrationTest;
import emsi.iir4.pathogene.domain.Detection;
import emsi.iir4.pathogene.repository.DetectionRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link DetectionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DetectionResourceIT {

    private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_VALIDATION = false;
    private static final Boolean UPDATED_VALIDATION = true;

    private static final String DEFAULT_STADE = "AAAAAAAAAA";
    private static final String UPDATED_STADE = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/detections";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DetectionRepository detectionRepository;

    @Mock
    private DetectionRepository detectionRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDetectionMockMvc;

    private Detection detection;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Detection createEntity(EntityManager em) {
        Detection detection = new Detection()
            .photo(DEFAULT_PHOTO)
            .photoContentType(DEFAULT_PHOTO_CONTENT_TYPE)
            .code(DEFAULT_CODE)
            .validation(DEFAULT_VALIDATION)
            .stade(DEFAULT_STADE)
            .date(DEFAULT_DATE)
            .description(DEFAULT_DESCRIPTION);
        return detection;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Detection createUpdatedEntity(EntityManager em) {
        Detection detection = new Detection()
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .code(UPDATED_CODE)
            .validation(UPDATED_VALIDATION)
            .stade(UPDATED_STADE)
            .date(UPDATED_DATE)
            .description(UPDATED_DESCRIPTION);
        return detection;
    }

    @BeforeEach
    public void initTest() {
        detection = createEntity(em);
    }

    @Test
    @Transactional
    void createDetection() throws Exception {
        int databaseSizeBeforeCreate = detectionRepository.findAll().size();
        // Create the Detection
        restDetectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(detection)))
            .andExpect(status().isCreated());

        // Validate the Detection in the database
        List<Detection> detectionList = detectionRepository.findAll();
        assertThat(detectionList).hasSize(databaseSizeBeforeCreate + 1);
        Detection testDetection = detectionList.get(detectionList.size() - 1);
        assertThat(testDetection.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testDetection.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
        assertThat(testDetection.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testDetection.getValidation()).isEqualTo(DEFAULT_VALIDATION);
        assertThat(testDetection.getStade()).isEqualTo(DEFAULT_STADE);
        assertThat(testDetection.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testDetection.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createDetectionWithExistingId() throws Exception {
        // Create the Detection with an existing ID
        detection.setId(1L);

        int databaseSizeBeforeCreate = detectionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDetectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(detection)))
            .andExpect(status().isBadRequest());

        // Validate the Detection in the database
        List<Detection> detectionList = detectionRepository.findAll();
        assertThat(detectionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDetections() throws Exception {
        // Initialize the database
        detectionRepository.saveAndFlush(detection);

        // Get all the detectionList
        restDetectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(detection.getId().intValue())))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].validation").value(hasItem(DEFAULT_VALIDATION.booleanValue())))
            .andExpect(jsonPath("$.[*].stade").value(hasItem(DEFAULT_STADE)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }



    @Test
    @Transactional
    void getDetection() throws Exception {
        // Initialize the database
        detectionRepository.saveAndFlush(detection);

        // Get the detection
        restDetectionMockMvc
            .perform(get(ENTITY_API_URL_ID, detection.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(detection.getId().intValue()))
            .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo").value(Base64Utils.encodeToString(DEFAULT_PHOTO)))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.validation").value(DEFAULT_VALIDATION.booleanValue()))
            .andExpect(jsonPath("$.stade").value(DEFAULT_STADE))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingDetection() throws Exception {
        // Get the detection
        restDetectionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDetection() throws Exception {
        // Initialize the database
        detectionRepository.saveAndFlush(detection);

        int databaseSizeBeforeUpdate = detectionRepository.findAll().size();

        // Update the detection
        Detection updatedDetection = detectionRepository.findById(detection.getId()).get();
        // Disconnect from session so that the updates on updatedDetection are not directly saved in db
        em.detach(updatedDetection);
        updatedDetection
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .code(UPDATED_CODE)
            .validation(UPDATED_VALIDATION)
            .stade(UPDATED_STADE)
            .date(UPDATED_DATE)
            .description(UPDATED_DESCRIPTION);

        restDetectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDetection.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedDetection))
            )
            .andExpect(status().isOk());

        // Validate the Detection in the database
        List<Detection> detectionList = detectionRepository.findAll();
        assertThat(detectionList).hasSize(databaseSizeBeforeUpdate);
        Detection testDetection = detectionList.get(detectionList.size() - 1);
        assertThat(testDetection.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testDetection.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testDetection.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testDetection.getValidation()).isEqualTo(UPDATED_VALIDATION);
        assertThat(testDetection.getStade()).isEqualTo(UPDATED_STADE);
        assertThat(testDetection.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testDetection.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingDetection() throws Exception {
        int databaseSizeBeforeUpdate = detectionRepository.findAll().size();
        detection.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDetectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, detection.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(detection))
            )
            .andExpect(status().isBadRequest());

        // Validate the Detection in the database
        List<Detection> detectionList = detectionRepository.findAll();
        assertThat(detectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDetection() throws Exception {
        int databaseSizeBeforeUpdate = detectionRepository.findAll().size();
        detection.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDetectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(detection))
            )
            .andExpect(status().isBadRequest());

        // Validate the Detection in the database
        List<Detection> detectionList = detectionRepository.findAll();
        assertThat(detectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDetection() throws Exception {
        int databaseSizeBeforeUpdate = detectionRepository.findAll().size();
        detection.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDetectionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(detection)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Detection in the database
        List<Detection> detectionList = detectionRepository.findAll();
        assertThat(detectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDetectionWithPatch() throws Exception {
        // Initialize the database
        detectionRepository.saveAndFlush(detection);

        int databaseSizeBeforeUpdate = detectionRepository.findAll().size();

        // Update the detection using partial update
        Detection partialUpdatedDetection = new Detection();
        partialUpdatedDetection.setId(detection.getId());

        partialUpdatedDetection
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .code(UPDATED_CODE)
            .validation(UPDATED_VALIDATION)
            .stade(UPDATED_STADE)
            .description(UPDATED_DESCRIPTION);

        restDetectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDetection.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDetection))
            )
            .andExpect(status().isOk());

        // Validate the Detection in the database
        List<Detection> detectionList = detectionRepository.findAll();
        assertThat(detectionList).hasSize(databaseSizeBeforeUpdate);
        Detection testDetection = detectionList.get(detectionList.size() - 1);
        assertThat(testDetection.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testDetection.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testDetection.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testDetection.getValidation()).isEqualTo(UPDATED_VALIDATION);
        assertThat(testDetection.getStade()).isEqualTo(UPDATED_STADE);
        assertThat(testDetection.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testDetection.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateDetectionWithPatch() throws Exception {
        // Initialize the database
        detectionRepository.saveAndFlush(detection);

        int databaseSizeBeforeUpdate = detectionRepository.findAll().size();

        // Update the detection using partial update
        Detection partialUpdatedDetection = new Detection();
        partialUpdatedDetection.setId(detection.getId());

        partialUpdatedDetection
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .code(UPDATED_CODE)
            .validation(UPDATED_VALIDATION)
            .stade(UPDATED_STADE)
            .date(UPDATED_DATE)
            .description(UPDATED_DESCRIPTION);

        restDetectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDetection.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDetection))
            )
            .andExpect(status().isOk());

        // Validate the Detection in the database
        List<Detection> detectionList = detectionRepository.findAll();
        assertThat(detectionList).hasSize(databaseSizeBeforeUpdate);
        Detection testDetection = detectionList.get(detectionList.size() - 1);
        assertThat(testDetection.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testDetection.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testDetection.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testDetection.getValidation()).isEqualTo(UPDATED_VALIDATION);
        assertThat(testDetection.getStade()).isEqualTo(UPDATED_STADE);
        assertThat(testDetection.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testDetection.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingDetection() throws Exception {
        int databaseSizeBeforeUpdate = detectionRepository.findAll().size();
        detection.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDetectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, detection.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(detection))
            )
            .andExpect(status().isBadRequest());

        // Validate the Detection in the database
        List<Detection> detectionList = detectionRepository.findAll();
        assertThat(detectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDetection() throws Exception {
        int databaseSizeBeforeUpdate = detectionRepository.findAll().size();
        detection.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDetectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(detection))
            )
            .andExpect(status().isBadRequest());

        // Validate the Detection in the database
        List<Detection> detectionList = detectionRepository.findAll();
        assertThat(detectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDetection() throws Exception {
        int databaseSizeBeforeUpdate = detectionRepository.findAll().size();
        detection.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDetectionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(detection))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Detection in the database
        List<Detection> detectionList = detectionRepository.findAll();
        assertThat(detectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDetection() throws Exception {
        // Initialize the database
        detectionRepository.saveAndFlush(detection);

        int databaseSizeBeforeDelete = detectionRepository.findAll().size();

        // Delete the detection
        restDetectionMockMvc
            .perform(delete(ENTITY_API_URL_ID, detection.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Detection> detectionList = detectionRepository.findAll();
        assertThat(detectionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
