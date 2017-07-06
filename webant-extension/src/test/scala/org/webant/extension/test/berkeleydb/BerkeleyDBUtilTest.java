package org.webant.extension.test.berkeleydb;

import com.sleepycat.je.DatabaseException;
import org.junit.Before;
import org.junit.Test;
import org.webant.commons.entity.HttpDataEntity;
import org.webant.extension.berkeleydb.BerkeleydbDatabaseObjectImpl;

import static org.junit.Assert.assertEquals;

public class BerkeleyDBUtilTest {

    private BerkeleyDBUtil dbUtil = null;

    @Before
    public void setup() throws DatabaseException {
        dbUtil = new BerkeleyDBUtil("../data/bdb");
    }


    @Test
    public void testWriteToDatabase() {
        for (int i = 0; i < 10; i++){
            dbUtil.writeToDatabase(i+"", "学生"+i, true);
        }
    }

    @Test
    public void testReadFromDatabase() throws DatabaseException {
        String value = dbUtil.readFromDatabase("2");
        assertEquals(value, "学生2");
    }

    @Test
    public void testGetEveryItem() throws DatabaseException {
        int size = dbUtil.getEveryItem().size();
        assertEquals(size, 10);
    }

    @Test
    public void testDeleteFromDatabase() throws DatabaseException {
        dbUtil.deleteFromDatabase("4");
        assertEquals(9, dbUtil.getEveryItem().size());
    }

    public void cleanup() throws DatabaseException {
        dbUtil.closeDB();
    }

    @Test
    public void getBdbData() throws DatabaseException {
        BerkeleydbDatabaseObjectImpl bdb = new BerkeleydbDatabaseObjectImpl();
        bdb.openConnection("D:\\workspace\\webant\\data\\bdb", "webant");
        HttpDataEntity data = (HttpDataEntity) bdb.get("7a5aad0e093bfe8786c3b024f9e87594");
        System.out.println(data.srcUrl());
    }
}