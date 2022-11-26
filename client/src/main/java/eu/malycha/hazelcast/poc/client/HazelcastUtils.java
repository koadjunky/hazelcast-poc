package eu.malycha.hazelcast.poc.client;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.sql.SqlResult;

public class HazelcastUtils {
    public static void dropMapping(HazelcastInstance hz, String name) {
        String query = "DROP MAPPING IF EXISTS " + name;
        SqlResult result = hz.getSql().execute(query);
        result.close();
    }

    public static void configureMapping(HazelcastInstance hz, String name, Class<?> clazz) {
        String query =
            "CREATE MAPPING " + name + " " +
                "TYPE IMap " +
                "OPTIONS (" +
                "'keyFormat' = 'java'," +
                "'keyJavaClass' = 'java.lang.String'," +
                "'valueFormat' = 'java'," +
                "'valueJavaClass' = '" + clazz.getName() + "'" +
                ")";
        dropMapping(hz, name);
        SqlResult result = hz.getSql().execute(query);
        result.close();
    }

}
