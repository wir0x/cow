package ch.swissbytes.module.buho.app.configuration.model;

import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "configurations", uniqueConstraints = {
        @UniqueConstraint(name = "idx_configuration_key", columnNames = {"key_"})
})
@Getter
@Setter
@ToString
public class Configuration{

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key_")
    private String key;

    @Column(name = "value")
    private String value;

    @Column(name = "description")
    private String description;

    @Transient
    public static Configuration createNew() {
        Configuration configuration = new Configuration();
        configuration.id = EntityUtil.DEFAULT_LONG;
        configuration.key = EntityUtil.DEFAULT_STRING;
        configuration.value = EntityUtil.DEFAULT_STRING;
        configuration.description = EntityUtil.DEFAULT_STRING;

        return configuration;
    }
}
