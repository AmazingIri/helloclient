import com.datastax.driver.core.*;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.TypeCodec;
import com.datastax.driver.core.exceptions.InvalidTypeException;
import gheap.GHeap;

import java.io.IOException;
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

        int n = 1000000;
        int step = 1;
        boolean isUsingGHeap = true;

        if (isUsingGHeap) {
            ResultSet results = session.execute(String.format("SELECT * FROM test.s WHERE id = 1 allow filtering", n));
            for (Row row : results) {
                try {
                    ByteBuffer buf = row.getBytesUnsafe("info");
                    byte[] b = new byte[buf.remaining()];
                    buf.duplicate().get(b);
                    System.out.println(bytesToHex(b));
                    Student stu = (Student) gheap.GHeap.deserialize(b, GHeap.SKIP_TRANSIENT_FIELDS);
                    System.out.println(stu.name);
                } catch (IOException e) {
                    throw new InvalidTypeException(e.getMessage(), e);
                }
            }
        }
        else {
            ResultSet results = session.execute(String.format("SELECT * FROM test.t WHERE id <= %d allow filtering", n));
            CodecRegistry codecRegistry = cluster.getConfiguration().getCodecRegistry();
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


        //for (int i = 0; i < 100000; i+=2000) {
        //for (int i = 0; i <= 10000; i++) {
        //ResultSet results = session.execute(String.format("SELECT * FROM test.s WHERE id <= 1000000 allow filtering"));

        //ResultSet results = session.execute("SELECT * FROM test.t WHERE id = 2");
        //ResultSet results = session.execute(String.format("SELECT * FROM test.t WHERE id = %d", i));
        //String str = GenerateQueryStr(0, 99);
        //ResultSet results = session.execute(str);

        cluster.close();
    }
}