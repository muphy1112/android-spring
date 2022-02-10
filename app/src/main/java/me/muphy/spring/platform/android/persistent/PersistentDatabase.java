package me.muphy.spring.platform.android.persistent;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import me.muphy.spring.platform.android.AndroidContextHolder;

@Database(entities = {PersistentEntity.class}, version = 1, exportSchema = false)
public abstract class PersistentDatabase extends RoomDatabase {
    private static PersistentDatabase instance;

    public static PersistentDatabase getDatabase() {
        if (instance == null) {
            synchronized (PersistentDatabase.class) {
                if (instance == null) {
                    Context context = AndroidContextHolder.getContext().getApplicationContext();
                    instance = Room.databaseBuilder(context, PersistentDatabase.class, "persistence_database")
                            //.fallbackToDestructiveMigration() //暴力销毁原有数据
//                            .addMigrations(getMigration_1_to_2()) //数据版本的迁移
                            .build();
                }
            }
        }
        return instance;
    }

    public abstract PersistentDao getDao();
}
