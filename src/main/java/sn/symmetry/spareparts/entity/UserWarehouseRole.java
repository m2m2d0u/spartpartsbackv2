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
 * UserWarehouseRole entity linking users to roles for specific warehouses.
 * Allows users to have different roles in different warehouses.
 */
@Entity
@Table(name = "user_warehouse_role", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_warehouse_role", columnNames = {"user_warehouse_id", "role_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserWarehouseRole {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_warehouse_id", nullable = false)
    private UserWarehouse userWarehouse;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
