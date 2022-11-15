package com.seitov.messenger.entity;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(uniqueConstraints = { @UniqueConstraint(name = "UniqueUserAndChannel", columnNames = {"user_id", "channel_id"})})
public class Membership {
 
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-char")
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Channel channel;
    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        founder(3), admin(2), moderator(1), user(0);
        
        private int value;

        private Role(int value) {
            this.value = value;
        }

        public int getValue(){
            return value;
        }
    }

    public Membership(User user, Channel channel, Role role) {
        this.user = user;
        this.channel = channel;
        this.role = role;
    }

}
