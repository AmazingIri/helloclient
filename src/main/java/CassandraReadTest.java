import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;

/**
 * @author zxb 2014年12月29日 下午3:46:23
 */
public class CassandraReadTest {

    public static void main(String[] args) {
        Builder builder = Cluster.builder();
        builder.addContactPoint("127.0.0.1");

        // socket 链接配置
        // 为了调度时不至于很快中断，把超时时间设的长一点
        SocketOptions socketOptions = new SocketOptions().setKeepAlive(true).setConnectTimeoutMillis(5 * 10000).setReadTimeoutMillis(100000);
        builder.withSocketOptions(socketOptions);
        Cluster cluster = builder.build();
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());
        for (Host host : metadata.getAllHosts()) {
            System.out.printf("Data center: %s; Host: %s; Rack: %s\n", host.getDatacenter(), host.getAddress(),
                    host.getRack());
        }

        Session session = cluster.connect();

        ResultSet results = session.execute("SELECT * FROM test.t");// where id = 1");
        for (Row row : results) {
            System.out.println(String.format("%-10s\t%-10s\t%-20s", row.getInt("id"), row.getInt("age"),
                    row.getList("names", String.class)));
        }
        cluster.close();
    }
}