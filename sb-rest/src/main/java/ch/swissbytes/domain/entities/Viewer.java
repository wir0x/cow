package ch.swissbytes.domain.entities;

import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.LongUtil;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "viewer")
@Getter
@Setter
public class Viewer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "imei", nullable = false)
    private String imei;

    @Column(name = "gcm_token")
    private String gcmToken;

    @Column(name = "creation_date", nullable = false)
    private Date creationDate;

    @Column(name = "modification_date", nullable = false)
    private Date modificationDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_viewer_user_id"))
    private User user;

    public Viewer(Long id) {
        this.id = id;
    }

    public Viewer() {
    }

    public static Viewer linkUserToViewer(Optional<Viewer> viewerOpt, User user, String gcmToken, String uniqueId) {
        Viewer viewer = viewerOpt.isAbsent() ? createNew() : viewerOpt.get();
        viewer.setUser(user);
        viewer.setGcmToken(gcmToken == null || gcmToken.isEmpty() ? viewer.getGcmToken() : gcmToken);
        viewer.setImei(uniqueId == null || uniqueId.isEmpty() ? viewer.getImei() : uniqueId);
        viewer.setStatus(StatusEnum.ENABLED);
        return viewer;
    }

    public static Viewer createNew() {
        Viewer viewer = new Viewer();
        viewer.imei = EntityUtil.DEFAULT_STRING;
        viewer.gcmToken = EntityUtil.DEFAULT_STRING;
        viewer.creationDate = EntityUtil.DEFAULT_DATE;
        viewer.modificationDate = EntityUtil.DEFAULT_DATE;
        viewer.setStatus(StatusEnum.ENABLED);
        viewer.user = null;
        return viewer;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Viewer)) return false;

        Viewer other = (Viewer) obj;
        return this.user.getId() == other.user.getId();
    }

    public boolean isNew() {
        return LongUtil.isEmpty(id);
    }
}
