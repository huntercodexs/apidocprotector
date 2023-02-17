package com.apidocprotector.model;

import lombok.*;

import javax.persistence.*;

@Data
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "apidoc_protector_audit")
public class ApiDocProtectorAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    String username;

    @Column
    String level;

    @Column
    String token;

    @Column
    String tracker;

    @Column
    String detail;

    @Column
    String message;

    @Column
    String ip;

    @Column
    String createdAt;

}
