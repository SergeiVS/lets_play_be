package org.lets_play_be.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lets_play_be.entity.converters.RefreshTokenEncoder;

import java.time.OffsetDateTime;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Cacheable
@Table(name = "blacklisted_tokens")
public class BlacklistedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "owner_id")
    private AppUser owner;

    @Column(nullable = false, length = 512)
    @Convert(converter = RefreshTokenEncoder.class)
    private String refreshToken;

    @Column(nullable = false)
    private OffsetDateTime expiresAt;

    public BlacklistedToken(AppUser user, String refreshToken, OffsetDateTime expiresAt) {
        this.owner = user;
        this.expiresAt = expiresAt;
        this.refreshToken = refreshToken;
    }
}
