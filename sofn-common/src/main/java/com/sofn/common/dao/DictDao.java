package com.sofn.common.dao;

import com.sofn.common.db.BeanHandler;
import com.sofn.common.db.JdbcTemplate;
import com.sofn.common.model.Dict;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 字典查询Dao
 */
@Component
public class DictDao {

    /**
     * 根据字典类型和字典key获取字典
     * @param type 字典类型
     * @param key 字典key
     * @return 返回Dict简单字典对象
     */
    public Dict getByTypeAndKey(String type, String key) {
        String sql = "SELECT ID, DICTTYPE, DICTNAME, DICTCODE FROM SYS_DICT WHERE DICTTYPE = ? AND DICTCODE = ?";

        List<Dict> list = JdbcTemplate.query(sql, new BeanHandler<>(Dict.class), type, key);
        if (list != null && list.size()>0){
            return list.get(0);
        }

        return null;
    }

    /**
     * 根据字典类型获取字典
     * @param type 字典类型
     * @return 返回Dict简单字典列表
     */
    public List<Dict> getByType(String type) {
        String sql = "SELECT ID, DICTTYPE, DICTNAME, DICTCODE FROM SYS_DICT WHERE DICTTYPE = ?";
        return JdbcTemplate.query(sql, new BeanHandler<>(Dict.class), type);
    }

    /**
     * 根据字典类型和字典key获取字典
     * @param type 字典类型
     * @param value 字典key
     * @return 返回Dict简单字典对象
     */
    public Dict getByTypeAndValue(String type, String value) {
        String sql = "SELECT ID, DICTTYPE, DICTNAME, DICTCODE FROM SYS_DICT WHERE DICTTYPE = ? AND DICTNAME = ?";
        List<Dict> list = JdbcTemplate.query(sql, new BeanHandler<>(Dict.class), type, value);
        if (list != null && list.size()>0){
            return list.get(0);
        }

        return null;
    }


}
