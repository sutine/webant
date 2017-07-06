package org.webant.extension.berkeleydb;

import com.sleepycat.je.*;

import java.io.*;

/**
 * 通过database对象直接操作
 * */
public class BerkeleydbDatabaseObjectImpl<T> implements BerkeleydbDao<T> {

    Environment env = null;
    private Database database = null;

    @Override
    public void openConnection(String filePath, String databaseName) throws DatabaseException {
        File file = new File(filePath);
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        envConfig.setTransactional(true);
        env = new Environment(file, envConfig);
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setAllowCreate(true);
        databaseConfig.setTransactional(true);
        database = env.openDatabase(null, databaseName, databaseConfig);
    }

    @Override
    public void closeConnection() throws DatabaseException {
        if(database != null){
            database.close();
            if(env != null){
                env.cleanLog();
                env.close();
            }
        }
    }

    @Override
    public void delete(String name) throws DatabaseException {
        DatabaseEntry keyEntry = new DatabaseEntry();
        keyEntry.setData(name.getBytes());
        database.delete(null, keyEntry);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(String name) throws DatabaseException {
        T t = null;
        DatabaseEntry keyEntry = new DatabaseEntry();
        DatabaseEntry valueEntry = new DatabaseEntry();
        keyEntry.setData(name.getBytes());
        if(database.get(null, keyEntry, valueEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS){
            ByteArrayInputStream bais = new ByteArrayInputStream(valueEntry.getData());
            try {
                ObjectInputStream ois = new ObjectInputStream(bais);
                t = (T) ois.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    @Override
    public void save(String name, T t) throws DatabaseException {
        DatabaseEntry keyEntry = new DatabaseEntry();
        DatabaseEntry valueEntry = new DatabaseEntry();
        keyEntry.setData(name.getBytes());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(t);
        } catch (IOException e) {
            e.printStackTrace();
        }
        valueEntry.setData(baos.toByteArray());
        database.put(null, keyEntry, valueEntry);
    }

    @Override
    public void update(String name, T t) throws DatabaseException {
        save(name, t);
    }
}