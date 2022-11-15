package com.seitov.messenger.entity;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "UniqueNameAndChannel", columnNames = {"name", "channel_id"})})
public class Room {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-char")
    private UUID id;
    @Length(min=1, max = 50)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Channel channel;

}
