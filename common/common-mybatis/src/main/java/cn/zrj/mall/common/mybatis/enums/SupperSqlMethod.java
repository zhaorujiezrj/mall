package cn.zrj.mall.common.mybatis.enums;

/**
 * @description: SupperSqlMethod
 * @author: zhaorujie
 * @date: 2023/7/6
 * @version: v1.0
 **/
public enum SupperSqlMethod {

    /**
     * 插入
     */
    INSERT_IGNORE_ONE("insertIgnore", "插入一条数据（选择字段插入），如果中已经存在相同的记录，则忽略当前新数据", "<script>\nINSERT IGNORE INTO %s %s VALUES %s\n</script>"),


    ;
    private final String method;
    private final String desc;
    private final String sql;

    SupperSqlMethod(String method, String desc, String sql) {
        this.method = method;
        this.desc = desc;
        this.sql = sql;
    }

    public String getMethod() {
        return method;
    }

    public String getDesc() {
        return desc;
    }

    public String getSql() {
        return sql;
    }
}
