package com.example.lasse.heartratemonitor.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Database model for Measurement.
 */
@Entity(nameInDb = "measurement")
public class Measurement {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "timestamp")
    private Long timestamp;

    @Generated(hash = 310189316)
    public Measurement(Long id, Long timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    @Generated(hash = 1439585572)
    public Measurement() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
