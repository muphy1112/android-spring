package me.muphy.spring.platform.android.persistent;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PersistentDao {
    @Insert
    void inserts(List<PersistentEntity> entities);

    @Query("delete from persistent_entity")
    void deleteAll();

    @Query("delete from persistent_entity where id = :id")
    void deleteById(String id);

    @Query("delete from persistent_entity where category = :category and data_id = :dataId")
    void deleteByCategoryAndDataId(String category, String dataId);

    @Query("delete from persistent_entity where category = :category")
    void deleteAll(String category);

    @Query("select * from persistent_entity order by id")
    List<PersistentEntity> selectAll();

    @Query("select * from persistent_entity where id = :id")
    PersistentEntity selectById(String id);

    @Query("select * from persistent_entity where category = :category and data_id = :dataId order by id")
    PersistentEntity selectByCategoryAndDataId(String category, String dataId);

    @Query("select * from persistent_entity where category = :category order by id")
    List<PersistentEntity> selectAll(String category);
}
