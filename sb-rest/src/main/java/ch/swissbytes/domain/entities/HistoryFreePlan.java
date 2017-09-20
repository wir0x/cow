package ch.swissbytes.domain.entities;

import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Class to have control over all devices that acquired free service plan.
 * for not show when the user delete de app tracker o disabled data of the app
 */

@Entity
@Table(name = "history_free_plan", uniqueConstraints = {@UniqueConstraint(name = "idx_history_free_plan_uniqueId", columnNames = {"uniqueId"})})
@Getter
@Setter
public class HistoryFreePlan {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uniqueId")
    private String uniqueId;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "plan_id")
    private Long planId;

    @Column(name = "plan_name")
    private String planName;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Column(name = "date")
    private Date date;

    public static HistoryFreePlan createNew() {
        HistoryFreePlan historyFreePlan = new HistoryFreePlan();
        historyFreePlan.uniqueId = EntityUtil.DEFAULT_STRING;
        historyFreePlan.deviceName = EntityUtil.DEFAULT_STRING;
        historyFreePlan.planId = EntityUtil.DEFAULT_LONG;
        historyFreePlan.planName = EntityUtil.DEFAULT_STRING;
        historyFreePlan.status = StatusEnum.ENABLED;
        historyFreePlan.date = EntityUtil.DEFAULT_DATE;
        return historyFreePlan;
    }
}
