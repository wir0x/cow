package ch.swissbytes.module.demo.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "demo_positions")
@Getter
@Setter
public class DemoPosition {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    @Column(name = "extended_info", columnDefinition = "TEXT")
    private String extendedInfo;

    @Column(name = "device_id")
    private Long deviceId;
}
