package ch.swissbytes.module.shared.notifications.push_notification;

import ch.swissbytes.domain.dto.ViewerDto;
import ch.swissbytes.domain.entities.Viewer;
import ch.swissbytes.module.shared.gcm.GcmContentModel;
import com.google.gson.Gson;

public class PushNotificationHelper {

    public static String makeContent(Viewer viewer, ViewerDto data) {
        GcmContentModel gcmContentModel = new GcmContentModel();
        gcmContentModel.addRegId(viewer.getGcmToken());
        gcmContentModel.createData(data);
        return new Gson().toJson(gcmContentModel);
    }
}
