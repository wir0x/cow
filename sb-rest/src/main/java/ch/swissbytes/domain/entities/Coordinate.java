package ch.swissbytes.domain.entities;

import ch.swissbytes.module.buho.app.geofence.model.GeoFence;
import ch.swissbytes.module.shared.utils.LongUtil;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "coordinate")
@Getter
@Setter
public class Coordinate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "fence_id", foreignKey = @ForeignKey(name = "fk_coordinate_fence_id"))
    private GeoFence geoFence;

    public Coordinate() {
    }

    @Transient
    public boolean isNew() {
        return LongUtil.isEmpty(id);
    }

}
