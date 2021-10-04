package tgtools.tasklibrary.entity;


/**
 * @author tianjing
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
