package sn.symmetry.spareparts.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.symmetry.spareparts.enums.WarehousePermission;

import java.util.UUID;

@Entity
@Table(name = "user_warehouse_permission", uniqueConstraints = {
        @UniqueConstraint(name = "uk_uwp", columnNames = {"user_warehouse_id", "permission"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserWarehousePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_warehouse_id", nullable = false)
    private UserWarehouse userWarehouse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WarehousePermission permission;
}
