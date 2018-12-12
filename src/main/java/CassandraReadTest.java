import com.datastax.driver.core.*;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.TypeCodec;
import java.nio.ByteBuffer;


public class CassandraReadTest {

    public static String GenerateQueryStr(int start, int end) {
        String str = "SELECT * FROM test.t WHERE id IN (";
        for (int i = start; i < end; i++) {
            str = str.concat(String.format("%d, ", i));
        }
        str = str.concat(String.format("%d)", end));
        return str;
    }

    public static void main(String[] args) {
        Builder builder = Cluster.builder();
        builder.addContactPoint("127.0.0.1");

        SocketOptions socketOptions = new SocketOptions().setKeepAlive(true);
        builder.withSocketOptions(socketOptions);
        Cluster cluster = builder.build();
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());
        for (Host host : metadata.getAllHosts()) {
            System.out.printf("Data center: %s; Host: %s; Rack: %s\n",
                    host.getDatacenter(), host.getAddress(), host.getRack());
        }

        CodecRegistry codecRegistry = new CodecRegistry();

        Session session = cluster.connect();


        //for (int i = 0; i < 100000; i+=2000) {
        for (int i = 0; i <= 10000; i++) {
            //ResultSet results = session.execute(String.format("SELECT * FROM test.t WHERE id <= 1000000 allow filtering"));

            ResultSet results = session.execute("SELECT * FROM test.t WHERE id = 2");
            //ResultSet results = session.execute(String.format("SELECT * FROM test.t WHERE id = %d", i));
            //String str = GenerateQueryStr(0, 99);
            //ResultSet results = session.execute(str);
            for (Row row : results) {
                ColumnDefinitions columnDefinitions = row.getColumnDefinitions();
                for (ColumnDefinitions.Definition def : columnDefinitions) {
                    ByteBuffer buf = row.getBytesUnsafe(def.getName());
                    if (buf != null) {
                        TypeCodec codec = codecRegistry.codecFor(def.getType());
                        codec.deserialize(buf, ProtocolVersion.V3);
                    }

                }
            }
        }

        cluster.close();
    }
}