package ch.swissbytes.domain.entities;

import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "service_plan")
@Getter
@Setter
public class ServicePlan {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @Column(name = "description", length = 150)
    private String description;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;

    @Column(name = "price", nullable = false)
    private Double price;

    public ServicePlan() {
    }

    public ServicePlan(Long id) {
        this.id = id;
    }

    @Transient
    public static ServicePlan createNew() {
        ServicePlan plan = new ServicePlan();
        plan.setId(EntityUtil.DEFAULT_LONG);
        plan.setName(EntityUtil.DEFAULT_STRING);
        plan.setDescription(EntityUtil.DEFAULT_STRING);
        plan.setDurationDays(EntityUtil.DEFAULT_INTEGER);
        plan.setDurationMonths(EntityUtil.DEFAULT_INTEGER);
        plan.setPrice(EntityUtil.DEFAULT_DOUBLE);
        return plan;
    }
}
