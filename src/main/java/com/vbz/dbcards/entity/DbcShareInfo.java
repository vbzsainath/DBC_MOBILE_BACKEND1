package com.vbz.dbcards.entity;

import com.vbz.dbcards.enums.ShareStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "dbc_share_info",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "shared_by_userid",
                                "shared_to_userid",
                                "shared_dbcid"
                        }
                )
        })
@Getter
@Setter
public class DbcShareInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dsid;

    @Column(name = "shared_by_userid", nullable = false)
    private Long sharedByUserid;

    @Column(name = "shared_to_userid", nullable = false)
    private Long sharedToUserid;

    @Column(name = "shared_dbcid", nullable = false)
    private Long sharedDbcId;

    @Column(name = "statuscode", nullable = false)
    private Boolean statuscode = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "share_status", nullable = false)
    private ShareStatus shareStatus;

    @Column(name = "createdby", nullable = false)
    private Long createdBy;

    @Column(name = "createdon", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "updatedby")
    private Long updatedBy;

    @Column(name = "updatedon")
    private LocalDateTime updatedOn;

    @Column(name = "deletedby")
    private Long deletedBy;

    @Column(name = "deletedon")
    private LocalDateTime deletedOn;

    @PrePersist
    protected void onCreate() {
        this.createdOn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedOn = LocalDateTime.now();
    }
}