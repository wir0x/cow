package ch.swissbytes.module.shared.gcm;

import ch.swissbytes.domain.dto.ViewerDto;
import ch.swissbytes.domain.enumerator.PushNotificationType;
import ch.swissbytes.module.shared.utils.DateUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GcmContentModel {
    private List<String> registration_ids;
    private Map<String, String> data;
    private String collapse_key;

    public void addRegId(String regId) {
        if (registration_ids == null) {
            registration_ids = new ArrayList<>();
        }
        registration_ids.add(regId);
    }

    public void createData(String title, String message, PushNotificationType notificationtype, Object innerData, Integer errorType) {
        if (data == null) {
            data = new HashMap<>();
        }
        data.put("title", title);
        data.put("message", message);
        data.put("errorCode", errorType.toString());
        data.put("type", notificationtype.getId().toString());
        data.put("subscription", new Gson().toJson(innerData));
    }

    public void createData(ViewerDto viewerDto) {
        if (data == null) {
            data = new HashMap<>();
        }
        data.put("type", viewerDto.getNotificationType().getId().toString());
        data.put("title", viewerDto.getTitle());
        data.put("message", viewerDto.getMessage());
        data.put("userId", viewerDto.getUserId());
        data.put("fenceId", viewerDto.getFenceId());
        data.put("deviceId", viewerDto.getDeviceId());
        data.put("deviceIcon", viewerDto.getDeviceIcon());
        data.put("fenceName", viewerDto.getFenceName());
        data.put("deviceName", viewerDto.getDeviceName());
        data.put("deviceType", viewerDto.getDeviceType());
        data.put("latitude", viewerDto.getLatitude());
        data.put("longitude", viewerDto.getLongitude());
        data.put("dateTime", DateUtil.getSimpleDateTime(viewerDto.getDateTime()));
    }

    public List<String> getRegistration_ids() {
        return registration_ids;
    }

    public void setRegistration_ids(List<String> registration_ids) {
        this.registration_ids = registration_ids;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public String getCollapse_key() {
        return collapse_key;
    }

    public void setCollapse_key(String collapse_key) {
        this.collapse_key = collapse_key;
    }
}
