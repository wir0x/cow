package ch.swissbytes.domain.entities;

import ch.swissbytes.domain.enumerator.AlertTypeEnum;
import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "position_pointer")
@Getter
@Setter
@ToString
public class PositionPointer {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "pointer")
    private Long pointer;

    @Column(name = "alert_type")
    @Enumerated(EnumType.STRING)
    private AlertTypeEnum alertType;

    public PositionPointer() {
    }

    @Transient
    public static PositionPointer createNew() {
        PositionPointer positionPointer = new PositionPointer();
        positionPointer.id = 1;
        positionPointer.pointer = EntityUtil.DEFAULT_LONG;
        positionPointer.alertType = AlertTypeEnum.FENCE;
        return positionPointer;
    }
}
