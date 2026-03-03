package sn.symmetry.spareparts.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "sequence_counter", uniqueConstraints = {
        @UniqueConstraint(name = "uk_sequence", columnNames = {"entity_type", "\"year\""})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SequenceCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "entity_type", nullable = false, length = 30)
    private String entityType;

    @Column(name = "\"year\"", nullable = false)
    private Integer year;

    @Column(name = "last_value", nullable = false)
    private Long lastValue = 0L;
}
