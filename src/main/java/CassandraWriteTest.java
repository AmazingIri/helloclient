import com.datastax.driver.core.*;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.exceptions.InvalidTypeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import gheap.GHeap;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CassandraWriteTest {

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static void main(String[] args) {
        Builder builder = Cluster.builder();
        builder.addContactPoint("127.0.0.1");

        SocketOptions socketOptions = new SocketOptions().setKeepAlive(true);//.setConnectTimeoutMillis(5 * 10000).setReadTimeoutMillis(100000);
        builder.withSocketOptions(socketOptions);
        Cluster cluster = builder.build();
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());

        ObjectMapper objectMapper = new ObjectMapper();
        Session session = cluster.connect();

        Random rnd = new Random();
        rnd.setSeed(System.currentTimeMillis());

        int n      = 1000000;
        int method = 1; // 1:GHeap, 2:Normal, 3:JSON & Jackson


        for (int i = 0; i < n; i++) {
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

            if (method == 1) {
                // write for table test.s, (id int, info blob)
                Student stu = new Student(System.currentTimeMillis(), name, items, courses, requires);
                //System.out.println(i + " " + stu.name + " " + stu.date +" " + stu.items + " " + stu.courses + " " + stu.requires);
                try {
                    byte array[] = gheap.GHeap.serialize(stu, GHeap.SKIP_TRANSIENT_FIELDS);
                    //System.out.printf("%d %s\n", i, bytesToHex(array));

                    session.execute("INSERT INTO test.s (id, info) VALUES (?, ?)",
                            i,
                            ByteBuffer.wrap(array));
                } catch (IOException e) {
                    throw new InvalidTypeException(e.getMessage(), e);
                }

            }
            else if (method == 2) {
                // write for table test.t, (id, date, name, items, courses, requires)
                session.execute("INSERT INTO test.t (id, date, name, items, courses, requires) VALUES (?, ?, ?, ?, ?, ?)",
                        i,
                        System.currentTimeMillis(),
                        name,
                        items,
                        courses,
                        requires
                );
            } else {
                // write for table test.j, (id int, info VARCHAR)
                DataObject obj = new DataObject(i, System.currentTimeMillis(), name, items, courses, requires);
                String str = "";
                try {
                    str = objectMapper.writeValueAsString(obj);
                } catch (JsonProcessingException e) {
                    return;
                }

                session.execute("INSERT INTO test.j (id, info) VALUES (?, ?)", i, str);
            }

        }
        cluster.close();
    }
}
