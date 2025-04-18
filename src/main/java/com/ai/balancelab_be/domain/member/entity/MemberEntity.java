package com.ai.balancelab_be.domain.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

@Entity
@Table(name = "TB_MEMBERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Comment("이메일")
    @Column(unique = true, nullable = false)
    private String email;

    @Comment("유저이름")
    private String membername;

    @Comment("나이")
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer age = 0; // 필드 초기화

    @Comment("키")
    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double height = 0.0;

    @Comment("몸무게")
    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double weight = 0.0;

    @Comment("성별")
    private String gender;

    @Comment("목표 몸무게")
    private Double goalWeight;

    @Comment("구글 고유 아이디")
    private String sub;

    @Comment("프로필이미지경로")
    private String profileImageUrl;

    @CreationTimestamp
    @Column(name = "reg_date")
    private LocalDateTime insDate;

    @Comment("활동수준")
    private String activityLevel;

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
