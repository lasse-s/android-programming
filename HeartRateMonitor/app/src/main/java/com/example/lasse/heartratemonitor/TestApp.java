package com.example.lasse.heartratemonitor;

import android.app.Application;

import com.example.lasse.heartratemonitor.database.DaoMaster;
import com.example.lasse.heartratemonitor.database.DaoSession;
import com.example.lasse.heartratemonitor.database.Measurement;

/**
 * Helper class for database connections.
 */
public class TestApp extends Application {

    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        mDaoSession =
                new DaoMaster(new DbOpenHelper(this, "greendao_demo.db").getWritableDb()).newSession();

        if(mDaoSession.getMeasurementDao().loadAll().size() == 0){
            mDaoSession.getMeasurementDao().insert(new Measurement(1L, System.currentTimeMillis()));
        }

    }

    /**
     * For getting dao session.
     * @return dao session
     */
    public DaoSession getDaoSession() {
        return mDaoSession;
    }

}
