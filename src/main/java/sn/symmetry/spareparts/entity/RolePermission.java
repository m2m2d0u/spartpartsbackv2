package sn.symmetry.spareparts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * RolePermission entity linking roles to permissions.
 * Defines which permissions are granted to each role.
 */
@Entity
@Table(name = "role_permission", uniqueConstraints = {
    @UniqueConstraint(name = "uk_role_permission", columnNames = {"role_id", "permission_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
