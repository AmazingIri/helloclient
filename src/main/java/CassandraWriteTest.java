import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;
import java.util.*;

public class CassandraWriteTest {

    public static void main(String[] args) {
        Builder builder = Cluster.builder();
        builder.addContactPoint("127.0.0.1");

        SocketOptions socketOptions = new SocketOptions().setKeepAlive(true);//.setConnectTimeoutMillis(5 * 10000).setReadTimeoutMillis(100000);
        builder.withSocketOptions(socketOptions);
        Cluster cluster = builder.build();
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());

        Session session = cluster.connect();
        session.execute("TRUNCATE TABLE test.t");

        Random rnd = new Random();
        rnd.setSeed(System.currentTimeMillis());

        for (int i = 0; i < 10000000; i++) {
            int id = rnd.nextInt();

            String name = RandomString.generate(16);

            List items = new ArrayList<String>();
            for (int j = 0; j < 50; j++) {
                items.add(RandomString.generate(6));
            }

            Map courses = new HashMap<String, Double>();
            for (int j = 0; j < 50; j++) {
                String k = RandomString.generate(4);
                Double v = rnd.nextDouble();
                courses.put(k, v);
            }

            Set requires = new HashSet();
            for (int j = 0; j < 20; j++) {
                requires.add(rnd.nextInt());
            }



            session.execute("INSERT INTO test.t (id, date, name, items, courses, requires) VALUES (?, ?, ?, ?, ?, ?)",
                    id,
                    System.currentTimeMillis(),
                    name,
                    items,
                    courses,
                    requires
            );

//            System.out.printf("row inserted, number %d\n", i);
        }
        cluster.close();
    }
}