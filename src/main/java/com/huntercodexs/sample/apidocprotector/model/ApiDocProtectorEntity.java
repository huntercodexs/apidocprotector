package com.huntercodexs.sample.apidocprotector.model;

import lombok.*;

import javax.persistence.*;

@Data
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "apidoc_protector")
public class ApiDocProtectorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    String name;

    @Column
    String username;

    @Column
    String email;

    @Column
    String role;

    @Column
    String password;

    @Column
    String token;

    @Column
    String active;

    @Column
    String sessionKey;

    @Column
    String sessionVal;

    @Column
    String sessionCreatedAt;

    @Column
    String createdAt;

    @Column
    String updatedAt;

    @Column
    String deletedAt;

}
