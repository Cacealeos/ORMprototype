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
        import java.sql.Connection;
        import java.sql.PreparedStatement;
        import java.sql.ResultSet;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

public class App {

    public static void main(String[] args) throws Exception {

        Book bk = new Book("Hippos", "Lots of hippo", 9999.99);

        bk.setId(9);
        Connection conn = null;

        processGetbyID(bk, conn);
        processPut(bk, conn);
        processGetAll(bk, conn);
        processPost(bk,conn);
        deleteGetbyID(bk,conn);

    }

    public static String processGetbyID(Object object, Connection credential) throws Exception {

        TableMeta TBI = new TableMeta();
        TBI.setTableName(object.getClass().getAnnotation(Table.class).name());
        TBI.setBaseRows(new HashMap<>());
        TBI.setIdRow(new HashMap<>());

        List<FieldType> FTL = new ArrayList<>();

        try {

            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
                System.out.println(propertyDescriptor);
                if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null) {
                    Field field = object.getClass().getDeclaredField(propertyDescriptor.getName());

                    System.out.println(field);
                    FieldType FT = new FieldType(
                            field,
                            propertyDescriptor.getReadMethod(),
                            propertyDescriptor.getWriteMethod()
                    );

                    Column column = FT.getField().getAnnotation(Column.class);
                    if (column != null) {
                        if (!column.primaryKey())
                            TBI.getBaseRows().put(column.name(), FT);
                        else
                            TBI.getIdRow().put(column.name(), FT);
                    }

                    //String str = propertyDescriptor.getValue(propertyDescriptor.getName()).toString();


                }
            }

        } catch (NoSuchFieldException | IntrospectionException e) {
            throw new Exception(e);
        }


        StringBuilder sqlWhereID = new StringBuilder(); //represents the entire list of object field values as a string. Not needed for Put Request?

        for (Map.Entry<String, FieldType> entry : TBI.getIdRow().entrySet()) {
            sqlWhereID.append(entry.getKey()).append(" = '");
            sqlWhereID.append(entry.getValue().getGetter().invoke(object)).append("'");
        }

        String sql = "SELECT * FROM " + TBI.getTableName() + " WHERE " + sqlWhereID + ";";
        System.out.println("\n" + sql + "\n");

        return sql;

    }

    public static String deleteGetbyID(Object object, Connection credential) throws Exception {

        TableMeta TBI = new TableMeta();
        TBI.setTableName(object.getClass().getAnnotation(Table.class).name());
        TBI.setBaseRows(new HashMap<>());
        TBI.setIdRow(new HashMap<>());

        List<FieldType> FTL = new ArrayList<>();

        try {

            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
                System.out.println(propertyDescriptor);
                if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null) {
                    Field field = object.getClass().getDeclaredField(propertyDescriptor.getName());

                    System.out.println(field);
                    FieldType FT = new FieldType(
                            field,
                            propertyDescriptor.getReadMethod(),
                            propertyDescriptor.getWriteMethod()
                    );

                    Column column = FT.getField().getAnnotation(Column.class);
                    if (column != null) {
                        if (!column.primaryKey())
                            TBI.getBaseRows().put(column.name(), FT);
                        else
                            TBI.getIdRow().put(column.name(), FT);
                    }

                    //String str = propertyDescriptor.getValue(propertyDescriptor.getName()).toString();


                }
            }

        } catch (NoSuchFieldException | IntrospectionException e) {
            throw new Exception(e);
        }


        StringBuilder sqlWhereID = new StringBuilder(); //represents the entire list of object field values as a string. Not needed for Put Request?

        for (Map.Entry<String, FieldType> entry : TBI.getIdRow().entrySet()) {
            sqlWhereID.append(entry.getKey()).append(" = '");
            sqlWhereID.append(entry.getValue().getGetter().invoke(object)).append("'");
        }

        String sql = "DELETE FROM " + TBI.getTableName() + " WHERE " + sqlWhereID + ";";
        System.out.println("\n" + sql + "\n");


        return sql;

    }
    public static String processGetAll(Object object, Connection credential) throws Exception {

        TableMeta TBI = new TableMeta();
        TBI.setTableName(object.getClass().getAnnotation(Table.class).name());


        return "Select * FROM " + TBI.getTableName();

    }


    public static String processPut(Object object, Connection credential) throws Exception {

        TableMeta TBI = new TableMeta();
        TBI.setTableName(object.getClass().getAnnotation(Table.class).name());
        TBI.setBaseRows(new HashMap<>());
        TBI.setIdRow(new HashMap<>());

        //List<FieldType> FTL = new ArrayList<>();

        try {

            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
                System.out.println(propertyDescriptor);
                if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null) {
                    Field field = object.getClass().getDeclaredField(propertyDescriptor.getName());

                    System.out.println(field);
                    FieldType FT = new FieldType(
                            field,
                            propertyDescriptor.getReadMethod(),
                            propertyDescriptor.getWriteMethod()
                    );

                    Column column = FT.getField().getAnnotation(Column.class);
                    if (column != null) {
                        if (!column.primaryKey())
                            TBI.getBaseRows().put(column.name(), FT);
                        else
                            TBI.getIdRow().put(column.name(), FT);
                    }

                    //String str = propertyDescriptor.getValue(propertyDescriptor.getName()).toString();


                }
            }

        } catch (NoSuchFieldException | IntrospectionException e) {
            throw new Exception(e);
        }


        StringBuilder sqlWhereID = new StringBuilder(); //represents the entire list of object field values as a string. Not needed for Put Request?
        StringBuilder sqlRows = new StringBuilder(); //represents the entire list of object fields string.
        // Length() -2 substring is applied in sql string creation to account for the appended delimiter string of: (", ")
        for (Map.Entry<String, FieldType> entry : TBI.getBaseRows().entrySet()) {
            sqlRows.append(entry.getKey()).append(" = '");
            sqlRows.append(entry.getValue().getGetter().invoke(object)).append("', ");
        }
        for (Map.Entry<String, FieldType> entry : TBI.getIdRow().entrySet()) {
            sqlWhereID.append(entry.getKey()).append(" = '");
            sqlWhereID.append(entry.getValue().getGetter().invoke(object)).append("'");
        }

        String sql = "";
        if (sqlRows.length() > 4) { //length() less than 4 means rows is empty since it is automatically appended with a 2 length string of: (", ")
            sql = "UPDATE " + TBI.getTableName() + " SET " + sqlRows.substring(0, sqlRows.length() - 3) +
                    "' WHERE " + sqlWhereID + ";";
            System.out.println("\n" + sql + "\n");
        } else {
            System.out.println("Could not create sql UPDATE query");
        }

//        PreparedStatement ps  = credential.prepareStatement(sql);
//        ResultSet rs = ps.executeQuery();
//        if(rs.next()) {
//            return "";
//        }

        return sql;
    }

    public static String processPost(Object object, Connection credential) throws Exception {

        TableMeta TBI = new TableMeta();
        TBI.setTableName(object.getClass().getAnnotation(Table.class).name());
        TBI.setBaseRows(new HashMap<>());
        TBI.setIdRow(new HashMap<>());

        //List<FieldType> FTL = new ArrayList<>();

        try {

            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
                System.out.println(propertyDescriptor);
                if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null) {
                    Field field = object.getClass().getDeclaredField(propertyDescriptor.getName());

                    System.out.println(field);
                    FieldType FT = new FieldType(
                            field,
                            propertyDescriptor.getReadMethod(),
                            propertyDescriptor.getWriteMethod()
                    );

                    Column column = FT.getField().getAnnotation(Column.class);
                    if (column != null) {
                        if (!column.primaryKey())
                            TBI.getBaseRows().put(column.name(), FT);
                        else
                            TBI.getIdRow().put(column.name(), FT);
                    }

                    //String str = propertyDescriptor.getValue(propertyDescriptor.getName()).toString();


                }
            }

        } catch (NoSuchFieldException | IntrospectionException e) {
            throw new Exception(e);
        }


        StringBuilder sqlWhereID = new StringBuilder(); //represents the entire list of object field values as a string. Not needed for Put Request?
        StringBuilder sqlRows = new StringBuilder(); //represents the entire list of object fields string.
        // Length() -2 substring is applied in sql string creation to account for the appended delimiter string of: (", ")
        for (Map.Entry<String, FieldType> entry : TBI.getBaseRows().entrySet()) {
            sqlRows.append(entry.getKey()).append(" = '");
            sqlRows.append(entry.getValue().getGetter().invoke(object)).append("', ");
        }
        for (Map.Entry<String, FieldType> entry : TBI.getIdRow().entrySet()) {
            sqlWhereID.append(entry.getKey()).append(" = '");
            sqlWhereID.append(entry.getValue().getGetter().invoke(object)).append("'");
        }

        String sql = "";
        if (sqlRows.length() > 4) { //length() less than 4 means rows is empty since it is automatically appended with a 2 length string of: (", ")
            sql = "INSERT INTO " + TBI.getTableName() + " VALUES( " + sqlRows.substring(0, sqlRows.length() - 3)
                    + "')"+ ";";
            System.out.println("\n" + sql + "\n");
        } else {
            System.out.println("Could not create sql UPDATE query");
        }

//        PreparedStatement ps  = credential.prepareStatement(sql);
//        ResultSet rs = ps.executeQuery();
//        if(rs.next()) {
//            return "";
//        }

        return sql;
    }
}

