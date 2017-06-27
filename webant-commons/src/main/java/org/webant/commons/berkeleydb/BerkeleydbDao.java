package org.webant.commons.berkeleydb;

import com.sleepycat.je.DatabaseException;

/**
 * Berkeley DB interface of DAO
 * */
public interface BerkeleydbDao<T> {

    /**
     * open database
     * */
    public void openConnection(String filePath, String databaseName) throws DatabaseException;

    /**
     * 关闭数据库
     * */
    public void closeConnection() throws DatabaseException;

    /**
     * insert
     * */
    public void save(String name, T t) throws DatabaseException;

    /**
     * delete
     * */
    public void delete(String name) throws DatabaseException;

    /**
     * update
     * */
    public void update(String name, T t) throws DatabaseException;

    /**
     * select
     * */
    public T get(String name) throws DatabaseException;

}
