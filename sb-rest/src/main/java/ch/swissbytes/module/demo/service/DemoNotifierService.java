package ch.swissbytes.module.demo.service;

import ch.swissbytes.module.buho.app.configuration.repository.ConfigurationRepository;
import ch.swissbytes.module.buho.app.device.repository.DeviceRepository;
import ch.swissbytes.module.demo.model.DemoPosition;
import ch.swissbytes.module.demo.repository.DemoPositionDao;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class DemoNotifierService{
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String[] IMEIS = {"111111111111111", "222222222222222", "333333333333333"};
    private static int PORT_GV300;
    private static String URL;
    private static Socket socket;

    @Inject
    Logger log;
    @Inject
    private DeviceRepository deviceRepository;
    @Inject
    private DemoPositionDao demoPositionDao;
    @Inject
    private ConfigurationRepository configurationRepository;

    private List<Long> deviceIds;
    private LocalDateTime lastUpdate = LocalDateTime.of(LocalDate.of(1970, 1, 1), LocalTime.now(ZoneOffset.UTC));

    public void startNotificationDaily() {
        if (deviceIds == null) {
            initialize();
        }

        try {
            log.info("start demo notification: " + new Date());
            findAndSendLatestDemoPositions();
            log.info("end demo notification: " + new Date());
        } catch (Exception e) {
            log.error("Error Demo background job  " + e.getMessage(), e);
        }
    }

    private void initialize() {
        log.info("init");
        deviceIds = new ArrayList<>();

        PORT_GV300 = KeyAppConfiguration.getInt(ConfigurationKey.PORT_GV300);
        URL = KeyAppConfiguration.getString(ConfigurationKey.URL_POSITION_SERVER);

        for (String imei : IMEIS) {
            Optional<Device> deviceOptional = deviceRepository.getByImei(imei);
            if (deviceOptional.isPresent()) {
                deviceIds.add(deviceOptional.get().getId());
            }
        }
    }

    private void findAndSendLatestDemoPositions() {
        LocalDateTime currentTime = LocalDateTime.of(LocalDate.of(1970, 1, 1), LocalTime.now(ZoneOffset.UTC));
        List<DemoPosition> positions = demoPositionDao.findByDeviceIdAndDate(deviceIds, lastUpdate, currentTime);
        for (DemoPosition pos : positions) {
            String command = pos.getExtendedInfo().replace("<date>", LocalDate.now().format(FORMAT));
            sendCommandToServer(command);
        }

        lastUpdate = LocalDateTime.of(LocalDate.of(1970, 1, 1), LocalTime.now(ZoneOffset.UTC));
    }

    private void sendCommandToServer(String command) {
        log.info("Sending command to position server: " + command);
        try {
            InetAddress address = InetAddress.getByName(URL);
            socket = new Socket(address, PORT_GV300);
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(command);
            bw.flush();
            socket.close();
        } catch (IOException e) {
            log.error("failing to send command to server: " + URL + ":" + PORT_GV300, e);
        }
    }
}
