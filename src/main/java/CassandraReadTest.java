import com.datastax.driver.core.*;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.TypeCodec;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;


public class CassandraReadTest {

    public static void main(String[] args) {
        Builder builder = Cluster.builder();
        builder.addContactPoint("127.0.0.1");

        SocketOptions socketOptions = new SocketOptions().setKeepAlive(true);
        builder.withSocketOptions(socketOptions);
        Cluster cluster = builder.build();
        CodecRegistry codeRegistry = cluster.getConfiguration().getCodecRegistry();
        JsonCodec<DataObject> codec = new JsonCodec<DataObject>(DataObject.class);
        codeRegistry.register(codec);
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());
        for (Host host : metadata.getAllHosts()) {
            System.out.printf("Data center: %s; Host: %s; Rack: %s\n",
                    host.getDatacenter(), host.getAddress(), host.getRack());
        }

        Session session = cluster.connect();
        ResultSet results = session.execute("SELECT * FROM test.t");

        for (Row row : results) {
            //System.out.println(results.one().getToken("json"));
            List<Integer> list = row.getList("json", Integer.class);
            Object var;
            byte[] bytes = new byte[list.size()];
            Iterator<Integer> integerIterator = list.iterator();
            int index = 0;
            while(integerIterator.hasNext()) {
                bytes[index] = integerIterator.next().byteValue();
            }
            var =  codec.deserialize(ByteBuffer.wrap(bytes), ProtocolVersion.V5);
            //System.out.println("%s", var);
        }
    }
}