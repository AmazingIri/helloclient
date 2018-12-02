import com.datastax.driver.core.*;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.TypeCodec;
import java.nio.ByteBuffer;


public class CassandraReadTest {

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

        Session session = cluster.connect();
        ResultSet results = session.execute("SELECT * FROM test.t");

        CodecRegistry codecRegistry = new CodecRegistry();
        for (Row row : results) {
            ColumnDefinitions columnDefinitions = row.getColumnDefinitions();
            Object var;
            for (ColumnDefinitions.Definition def : columnDefinitions) {
                ByteBuffer buf = row.getBytesUnsafe(def.getName());
                if (buf != null) {
                    TypeCodec codec = codecRegistry.codecFor(def.getType());
                    var = codec.deserialize(buf, ProtocolVersion.V3);
                }

            }
            //System.out.println("%s", var);
        }
        cluster.close();
    }
}