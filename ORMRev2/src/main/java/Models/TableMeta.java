package Models;

import java.util.Map;


public class TableMeta {

    private String tableName;
    private Map<String, FieldType> idRow;
    private Map<String, FieldType> baseRows;
//    private Map<String, iField> manyToOneRows;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, FieldType> getIdRow() {
        return idRow;
    }

    public void setIdRow(Map<String, FieldType> id) {
        this.idRow = id;
    }

    public Map<String, FieldType> getBaseRows() {
        return baseRows;
    }

    public void setBaseRows(Map<String, FieldType> baseRows) {
        this.baseRows = baseRows;
    }

//    public Map<String, ManyToOneMetaInfo> getManyToOneRows() {
//        return manyToOneRows;
//    }
//
//    public void setManyToOneRows(Map<String, ManyToOneMetaInfo> manyToOneRows) {
//        this.manyToOneRows = manyToOneRows;
//    }
}



