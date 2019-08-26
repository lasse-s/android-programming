package com.example.lasse.heartratemonitor.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Database model for Measurement Point.
 */
@Entity(nameInDb = "measurement_point")
public class MeasurementPoint {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "timestamp")
    private Long timestamp;

    private long measurementID;

    @ToOne(joinProperty = "measurementID")
    private Measurement measurement;

    @Property(nameInDb = "hr")
    private Integer hr;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1412008717)
    private transient MeasurementPointDao myDao;

    @Generated(hash = 1914321602)
    public MeasurementPoint(Long id, Long timestamp, long measurementID,
            Integer hr) {
        this.id = id;
        this.timestamp = timestamp;
        this.measurementID = measurementID;
        this.hr = hr;
    }

    @Generated(hash = 1114681010)
    public MeasurementPoint() {
    }

    @Generated(hash = 164497418)
    private transient Long measurement__resolvedKey;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public long getMeasurementID() {
        return this.measurementID;
    }

    public void setMeasurementID(long measurementID) {
        this.measurementID = measurementID;
    }

    public Integer getHr() {
        return this.hr;
    }

    public void setHr(Integer hr) {
        this.hr = hr;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 680531785)
    public Measurement getMeasurement() {
        long __key = this.measurementID;
        if (measurement__resolvedKey == null
                || !measurement__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MeasurementDao targetDao = daoSession.getMeasurementDao();
            Measurement measurementNew = targetDao.load(__key);
            synchronized (this) {
                measurement = measurementNew;
                measurement__resolvedKey = __key;
            }
        }
        return measurement;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 706263008)
    public void setMeasurement(@NotNull Measurement measurement) {
        if (measurement == null) {
            throw new DaoException(
                    "To-one property 'measurementID' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.measurement = measurement;
            measurementID = measurement.getId();
            measurement__resolvedKey = measurementID;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 613228725)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMeasurementPointDao() : null;
    }
}
