package Application;


import Models.Dog;
import Models.FieldType;
import Models.TableMeta;
import annotations.Column;
import annotations.Table;
import org.postgresql.util.PSQLException;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

public class App {

    public static void main(String[] args) throws Exception {

//        Dog bk = new Dog("Mike", "3", "labrador", "lazy", "black");
//        bk.setId(48);
//        Connection conn = null;
//
//        Properties props = new Properties();
//        try {
//
//            Class.forName("org.postgresql.Driver");
//
//            props.load(App.class.getClassLoader().getResourceAsStream("connection.properties"));
////               props.load(new FileInputStream(JDBCConnection.class.getClassLoader().getResource("connection.properties").getFile()));
////               props.load(new FileReader("src/main/resources/connection.properties"));
//
//            String endpoint = props.getProperty("endpoint");
//            //URL Format (Postgresql JDBC)
//            //jdbc:postgresql://[endpoint]/[database]
//            String url = "jdbc:postgresql://" + endpoint + "/postgres";
//            String username = props.getProperty("username");
//            String password = props.getProperty("password");
//
//            conn = DriverManager.getConnection(url, username, password);
//        }
//        catch (IOException | SQLException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        List<Dog> doggies = new ArrayList<>();
//        Dog dog = new Dog();
//
//
////        for (Object animal: processGetAll(dog, conn)) {
////            doggies.add((Dog) animal);
////        }
//        System.out.print(processGetbyID(bk, conn).getClass().getAnnotations());

//        processPost(bk,conn);
//        processGetbyID(bk, conn);
//        processPut(bk, conn);
//        processGetAll(bk, conn);
//        deleteGetbyID(bk,conn);

    }

    public static Object processPost(Object object, Connection conn) throws Exception {

        TableMeta TBI = new TableMeta();
        TBI.setTableName(object.getClass().getAnnotation(Table.class).name());
        TBI.setBaseRows(new HashMap<>());
        TBI.setIdRow(new HashMap<>());

        List<FieldType> fields = new ArrayList<FieldType>();

        try {

            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
                //System.out.println(propertyDescriptor);
                if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null) {
                    Field field = object.getClass().getDeclaredField(propertyDescriptor.getName());

                    //System.out.println(field);
                    FieldType FT = new FieldType(
                            field,
                            propertyDescriptor.getReadMethod(),
                            propertyDescriptor.getWriteMethod()
                    );
                    fields.add(FT); //Adds field to field list

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

        StringBuilder sqlRows = new StringBuilder(); //represents the entire list of object fields string.
        // Length() -2 substring is applied in sql string creation to account for the appended delimiter string of: (", ")
        StringBuilder sqlvalues = new StringBuilder();

        for (Map.Entry<String, FieldType> entry : TBI.getBaseRows().entrySet()) {
            sqlRows.append(entry.getKey()).append(", ");
            sqlvalues.append(entry.getValue().getGetter().invoke(object)).append("', '");
        }

        String sql = "";
        if (sqlRows.length() > 6) { //length() less than 6  means rows is empty since it is automatically appended with a 2 length string of: (", ")
            sql = "INSERT INTO " + TBI.getTableName() +
                    "(" + sqlRows.substring(0, sqlRows.length() - 2) + ") VALUES ('" +
                    sqlvalues.substring(0, sqlvalues.length() -3) +
                    ") RETURNING *;";
            System.out.println("\n" + sql + "\n");
        } else {
            System.out.println("Could not create sql POST query");
        }

        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        //Recreates Object from empty constructor
        Constructor struct = object.getClass().getConstructor();
        Object OB = struct.newInstance();
        //populates new object fields with returned values from SQL

        while (rs.next()) {
            //System.out.println(rs.getString(1));
            for (FieldType type : fields) {

                Column column = type.getField().getAnnotation(Column.class);

                //System.out.println(type.getSetter().getName());
                if (column != null) {
                    if (!column.primaryKey())
                        type.getSetter().invoke(OB, rs.getString(column.name()));
                    else
                        type.getSetter().invoke(OB, rs.getInt(column.name()));
                }

            }
        }
            System.out.println(OB);
        return OB;
    }
    public static Object processGetbyID(Object object, Connection conn) throws Exception {

        TableMeta TBI = new TableMeta();
        TBI.setTableName(object.getClass().getAnnotation(Table.class).name());
        TBI.setBaseRows(new HashMap<>());
        TBI.setIdRow(new HashMap<>());

        List <FieldType> fields  = new ArrayList<FieldType>();

        try {

            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
                //System.out.println(propertyDescriptor);
                if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null) {
                    Field field = object.getClass().getDeclaredField(propertyDescriptor.getName());

                    //System.out.println(field);
                    FieldType FT = new FieldType(
                            field,
                            propertyDescriptor.getReadMethod(),
                            propertyDescriptor.getWriteMethod()
                    );
                    fields.add(FT); //Adds field to field list

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

        PreparedStatement ps  = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        Object OB = Class.forName(object.getClass().getName()).newInstance();

        while(rs.next()) {

            Method M = null;
            //type.getSetter().invoke(OB, rs.getString(column.name()));

            for (FieldType type : fields) {
                Column column = type.getField().getAnnotation(Column.class);
                if (column != null) {
                    if (!column.primaryKey()){
                        M = OB.getClass().getDeclaredMethod(type.getSetter().getName(), String.class);
                        M.invoke(OB,rs.getString(column.name()));
                        System.out.println(rs.getString(column.name()));
                    }
                    else{
                        M = OB.getClass().getDeclaredMethod(type.getSetter().getName(), int.class);
                        M.invoke(OB,rs.getInt(column.name()));
                        System.out.println(rs.getString(column.name()));
                    }
                }
            }

        }
            System.out.println(OB);
        return OB;
    }
    public static void deleteGetbyID(Object object, Connection conn) throws Exception {

        TableMeta TBI = new TableMeta();
        TBI.setTableName(object.getClass().getAnnotation(Table.class).name());
        TBI.setBaseRows(new HashMap<>());
        TBI.setIdRow(new HashMap<>());

        try {

            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
                //System.out.println(propertyDescriptor);
                if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null) {
                    Field field = object.getClass().getDeclaredField(propertyDescriptor.getName());

                    //System.out.println(field);
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

        PreparedStatement ps  = conn.prepareStatement(sql);
        try {
            ps.executeQuery();
        } catch (PSQLException e) {

        }

    }
    public static List<Object> processGetAll(Object object, Connection conn) throws Exception {

        TableMeta TBI = new TableMeta();
        TBI.setTableName(object.getClass().getAnnotation(Table.class).name());

        List<FieldType> fields = new ArrayList<FieldType>();

        try {
            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
                //System.out.println(propertyDescriptor);
            if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null) {
                Field field = object.getClass().getDeclaredField(propertyDescriptor.getName());

                //System.out.println(field);
                FieldType FT = new FieldType(
                        field,
                        propertyDescriptor.getReadMethod(),
                        propertyDescriptor.getWriteMethod()
                );
                fields.add(FT); //Adds field to field list


            }
        }

    } catch (NoSuchFieldException | IntrospectionException e) {
        throw new Exception(e);
    }

        String sql = "Select * FROM " + TBI.getTableName();

        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        //Recreates Object from empty constructor

        List<Object> obs = new ArrayList<>();

        //populates new object fields with returned values from SQL
        while (rs.next()) {
            //Constructor struct = object.getClass().getConstructor();
            Object OB = Class.forName(object.getClass().getName()).newInstance();
            Method M = null;
            //type.getSetter().invoke(OB, rs.getString(column.name()));

            for (FieldType type : fields) {
                Column column = type.getField().getAnnotation(Column.class);
                if (column != null) {
                    if (!column.primaryKey()){
                        M = OB.getClass().getDeclaredMethod(type.getSetter().getName(), String.class);
                        M.invoke(OB,rs.getString(column.name()));
                        //System.out.println(rs.getString(column.name()));
                    }
                    else{
                        M = OB.getClass().getDeclaredMethod(type.getSetter().getName(), int.class);
                        M.invoke(OB,rs.getInt(column.name()));
                        //System.out.println(rs.getString(column.name()));
                    }
                }
            }
            obs.add(OB);
            //System.out.println(OB);
        }
        System.out.println(obs);
        return obs;

    }
    public static Object processPut(Object object, Connection conn) throws Exception {

        TableMeta TBI = new TableMeta();
        TBI.setTableName(object.getClass().getAnnotation(Table.class).name());
        TBI.setBaseRows(new HashMap<>());
        TBI.setIdRow(new HashMap<>());

        List<FieldType> fields = new ArrayList<FieldType>();

        try {

            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
                //System.out.println(propertyDescriptor);
                if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null) {
                    Field field = object.getClass().getDeclaredField(propertyDescriptor.getName());

                    //System.out.println(field);
                    FieldType FT = new FieldType(
                            field,
                            propertyDescriptor.getReadMethod(),
                            propertyDescriptor.getWriteMethod()
                    );
                    fields.add(FT); //Adds field to field list

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
                    "' WHERE " + sqlWhereID + " RETURNING *;";
            System.out.println("\n" + sql + "\n");
        } else {
            System.out.println("Could not create sql UPDATE query");
        }

        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        //Recreates Object from empty constructor
        Constructor struct = object.getClass().getConstructor();
        Object OB = struct.newInstance();
        //populates new object fields with returned values from SQL
        while (rs.next()) {

            for (FieldType type : fields) {
                Column column = type.getField().getAnnotation(Column.class);
                if (column != null) {
                    if (!column.primaryKey())
                        type.getSetter().invoke(OB, rs.getString(column.name()));
                    else
                        type.getSetter().invoke(OB, rs.getInt(column.name()));
                }
            }
        }
            System.out.println(object);
        return OB = object;
    }

//END APP
}
