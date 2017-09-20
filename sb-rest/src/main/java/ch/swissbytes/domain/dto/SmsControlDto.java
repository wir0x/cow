package ch.swissbytes.domain.dto;

import ch.swissbytes.domain.entities.SmsControl;
import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


@Getter
@Setter
public class SmsControlDto implements Serializable {

    private String actualMonth;
    private String usedSms;
    private String remainingSms;
    private String smsForMonth;

    public static SmsControlDto createNew() {
        SmsControlDto smsControlDto = new SmsControlDto();
        smsControlDto.actualMonth = EntityUtil.DEFAULT_STRING;
        smsControlDto.usedSms = EntityUtil.DEFAULT_STRING;
        smsControlDto.remainingSms = EntityUtil.DEFAULT_STRING;
        smsControlDto.smsForMonth = EntityUtil.DEFAULT_STRING;

        return smsControlDto;
    }

    public static SmsControlDto fromSmsControl(SmsControl smsControl) {
        SmsControlDto smsControlDto = SmsControlDto.createNew();
        smsControlDto.actualMonth = nameMonth(smsControl.getMonthYear());
        smsControlDto.usedSms = smsControl.getUsedSms().toString();
        smsControlDto.remainingSms = totalRemainingSms(smsControl);
        smsControlDto.smsForMonth = smsControl.getMaxSms().toString();

        return smsControlDto;
    }

    private static String totalRemainingSms(SmsControl smsControl) {
        int remainingSms = smsControl.getMaxSms() - smsControl.getUsedSms();
        return String.valueOf(remainingSms);
    }

    private static String nameMonth(Date date) {
        return capitalizeMonth(new SimpleDateFormat("MMMM").format(date));
    }

    private static String capitalizeMonth(String month) {
        return Character.toUpperCase(month.charAt(0)) + month.substring(1);
    }
}
