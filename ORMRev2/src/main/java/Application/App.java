package Application;

import Models.Book;
import Models.FieldType;
import Models.TableMeta;
import annotations.Column;
import annotations.Table;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {

    public static void main(String[] args) throws Exception {

        Book bk = new Book("Hippos", "Lots of hippo", 9999.99);

        TableMeta TBI = new TableMeta();
        TBI.setTableName(bk.getClass().getAnnotation(Table.class).name());
        TBI.setBaseRows(new HashMap<>());
        TBI.setIdRow(new HashMap<>());

        List<FieldType> FTL = null;

        try {
            List<FieldType> derpo = new ArrayList<>();
            for(PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(bk.getClass()).getPropertyDescriptors()){
                System.out.println(propertyDescriptor);
                if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null) {
                    Field field = bk.getClass().getDeclaredField(propertyDescriptor.getName());
                    System.out.println(field);
                    derpo.add(
                            new FieldType(
                                    field,
                                    propertyDescriptor.getReadMethod(),
                                    propertyDescriptor.getWriteMethod())
                    );
                }
            }
            FTL = derpo;
        } catch (NoSuchFieldException | IntrospectionException e) {
            throw new Exception(e);
        }

        for(FieldType field : FTL){
            Column column = field.getField().getAnnotation(Column.class);
            if(column != null)
            {
                if(!column.primaryKey())
                    TBI.getBaseRows().put(column.name(), field);
                else
                    TBI.getIdRow().put(column.name(), field);
            }
        }

        List<Object> values = new ArrayList<>();
        StringBuilder sqlRows = new StringBuilder();
        for(Map.Entry<String , FieldType> entry : TBI.getBaseRows().entrySet()){
            sqlRows.append(entry.getKey()).append(", ");
            values.add(entry.getValue().getGetter().invoke(bk));
        }

        StringBuilder sqlQuestionValues = new StringBuilder();
        for (int i = 0; i < TBI.getBaseRows().size() -1; i++) {
            sqlQuestionValues.append("?, ");
        }
        sqlQuestionValues.append("?");

        String sql;
        if (sqlRows.length() > 3) {
            sql = "INSERT INTO " + TBI.getTableName() + " (" + sqlRows.substring(0, sqlRows.length() - 2) + ") " +
                    " VALUES (" + sqlQuestionValues + ");";
            System.out.println("\n" +  sql + "\n");
        } else {
            System.out.println("Could not create sql INSERT query");
        }


    }

}
