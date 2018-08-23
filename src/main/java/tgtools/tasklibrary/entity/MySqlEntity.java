package tgtools.tasklibrary.entity;


/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：15:50
 */
public class MySqlEntity extends SqlEntity {

    private String m_HasDataSql;

    public String getHasDataSql() {
        return m_HasDataSql;
    }

    public void setHasDataSql(String p_HasDataSql) {
        m_HasDataSql = p_HasDataSql;
    }
}
