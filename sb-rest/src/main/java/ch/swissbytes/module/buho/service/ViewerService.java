package ch.swissbytes.module.buho.service;

import ch.swissbytes.domain.dao.ViewerDao;
import ch.swissbytes.domain.dto.SmartphoneDto;
import ch.swissbytes.domain.entities.Viewer;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.buho.app.user.repository.UserRepository;
import ch.swissbytes.module.shared.exception.DuplicateException;
import ch.swissbytes.module.shared.json.JsonReader;
import ch.swissbytes.module.shared.persistence.Optional;
import com.google.gson.JsonObject;
import org.jboss.logging.Logger;
import org.scribe.model.Response;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.Date;
import java.util.List;


@Stateless
public class ViewerService {

    @Inject
    private Logger log;
    @Inject
    private ViewerDao viewerDao;
    @Inject
    private UserRepository userRepository;

    public void disabled(Viewer viewer) throws EJBException {
        viewer.setStatus(StatusEnum.DISABLED);
        userRepository.merge(viewer);
        log.info("disabled viewer: " + viewer.getId());
    }

    public Viewer storeViewer(SmartphoneDto dto) throws RuntimeException {
        try {
            if (dto.getImei() == null) {
                return addByUUID(dto);
            }
            if (dto.getUuid() == null) {
                return addByImei(dto);
            }
            return Viewer.createNew();

        } catch (PersistenceException pEx) {
            log.error("[ERROR] PersistenceException: " + pEx.getMessage(), pEx);
            throw new DuplicateException(pEx);
        }
    }

    private Viewer addByImei(SmartphoneDto dto) {
        Optional<Viewer> viewerOptional = viewerDao.getByUniqueId(dto.getImei());
        Viewer viewer = Viewer.createNew();
        if (viewerOptional.isPresent()) {
            log.info("update viewer");
            viewer = viewerOptional.get();
            viewer.setGcmToken(dto.getGcmToken());
            viewer.setModificationDate(new Date());
        } else {
            log.info("create viewer");
            viewer.setImei(dto.getImei());
            viewer.setGcmToken(dto.getGcmToken());
        }
        return viewerDao.merge(viewer);
    }

    private Viewer addByUUID(SmartphoneDto dto) {
        Optional<Viewer> viewerOptional = viewerDao.getByUniqueId(dto.getUuid());
        Viewer viewer = Viewer.createNew();

        if (viewerOptional.isPresent()) {
            log.info("update viewer");
            viewer = viewerOptional.get();
            viewer.setGcmToken(dto.getGcmToken());
            viewer.setModificationDate(new Date());
        } else {
            log.info("create viewer");
            viewer.setImei(dto.getUuid());
            viewer.setGcmToken(dto.getGcmToken());
        }
        return viewerDao.merge(viewer);
    }

    public void updateViewer(Viewer viewer) throws RuntimeException {
        try {
            viewerDao.merge(viewer);

        } catch (PersistenceException pEx) {
            log.error("[ERROR] PersistenceException: " + pEx.getMessage(), pEx);
            throw new DuplicateException(pEx);
        }
    }

    /**
     * Method to check if gcm token is registered If not delete viewer
     *
     * @param response
     * @param data
     */
    public void checkResponse(Response response, String data) {
        JsonObject jsonObj = JsonReader.readAsJsonObject(response.getBody());
        Integer success = JsonReader.getIntegerOrNull(jsonObj, "success");
        Integer failure = JsonReader.getIntegerOrNull(jsonObj, "failure");

        if (success != null && failure != null && success == 0 && failure == 1) {
            removeViewer(jsonObj, data);
        }
    }

    private void removeViewer(JsonObject jsonObj, String data) {
        final String gcmToken = getGcmTokenFromData(data);
        final List<String> results = JsonReader.getSListOfLongOrNull(jsonObj, "results");

        if (isNotRegistered(results)) {
            Optional<Viewer> viewer = viewerDao.getByGcmToken(gcmToken);
            if (viewer.isPresent())
                viewerDao.remove(viewer.get());
        }
    }

    private String getGcmTokenFromData(String data) {
        JsonObject jsonObject = JsonReader.readAsJsonObject(data);
        return JsonReader.getStringOrNull(jsonObject, "registration_ids");
    }

    private boolean isNotRegistered(List<String> results) {
        JsonObject jsonObject = JsonReader.readAsJsonObject(results.get(0));
        final String error = JsonReader.getStringOrNull(jsonObject, "error");
        assert error != null;
        return error.equals("NotRegistered");
    }

    public List<Viewer> findByUserId(Long id) {
        return viewerDao.findByUserId(id);
    }

    public void linkUserToAppViewer(User user, String uniqueId, String gcmToken) {
        if (gcmToken != null && !gcmToken.isEmpty()) {
            Optional<Viewer> viewerOptional = viewerDao.getByGcmToken(gcmToken);
            Viewer viewer = Viewer.linkUserToViewer(viewerOptional, user, gcmToken, uniqueId);
            updateViewer(viewer);

        } else if (!uniqueId.isEmpty()) {
            Optional<Viewer> viewerOptional = viewerDao.getByUniqueId(uniqueId);
            Viewer viewer = Viewer.linkUserToViewer(viewerOptional, user, gcmToken, uniqueId);
            updateViewer(viewer);
        }
    }
}
