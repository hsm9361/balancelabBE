package com.ai.balancelab_be.domain.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Entity
@Table(name = "TB_MEMBERS")
@Getter
@Setter
public class MemberEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("이메일")
    @Column(unique = true, nullable = false)
    private String email;

    @Comment("유저이름")
    private String username;
    @Comment("나이")
    private Integer age;
    @Comment("키")
    private Double height;
    @Comment("몸무게")
    private Double weight;
    @Comment("성별")
    private String gender;
    @Comment("목표")
    private String goal;
    @Comment("구글 고유 아이디")
    private String sub;

    @CreationTimestamp
    @Column(name = "reg_date")
    private LocalDateTime insDate;

    @UpdateTimestamp
    @Column(name = "upd_date")
    private LocalDateTime updDate;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
