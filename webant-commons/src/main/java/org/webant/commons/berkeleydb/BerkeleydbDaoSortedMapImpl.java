package org.webant.commons.berkeleydb;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.*;

import java.io.File;

/**
 * 使用StoredMap实现
 * */
public class BerkeleydbDaoSortedMapImpl<T> implements BerkeleydbDao<T> {

    Environment env = null;
    private Database database = null;
    private StoredMap<String, T> storedMap = null;
    private Class<T> persistentClass = null;

    EntryBinding<String> keyBinding = null;
    SerialBinding<T> valueBinding = null;

    public BerkeleydbDaoSortedMapImpl(Class<T> persistentClass){
        this.persistentClass = persistentClass;
    }

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
        StoredClassCatalog catalog = new StoredClassCatalog(database);
        keyBinding = new SerialBinding<String>(catalog, String.class);
        valueBinding = new SerialBinding<T>(catalog, persistentClass);
        storedMap = new StoredMap<String, T>(database, keyBinding, valueBinding, true);
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
    public void delete(String name) {
        storedMap.remove(name);
    }

    @Override
    public T get(String name) {
        return storedMap.get(name);
    }

    @Override
    public void save(String name, T t) {
        storedMap.put(name, t);
    }

    @Override
    public void update(String name, T t) {
        save(name, t);
    }

}