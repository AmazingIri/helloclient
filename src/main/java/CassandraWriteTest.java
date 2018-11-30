import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;

/**
 * @author zxb 2015年1月29日 下午5:57:20
 */
public class CassandraWriteTest {

    public static void main(String[] args) {
        Builder builder = Cluster.builder();
        builder.addContactPoint("127.0.0.1");

        // socket 链接配置
        SocketOptions socketOptions = new SocketOptions().setKeepAlive(true);//.setConnectTimeoutMillis(5 * 10000).setReadTimeoutMillis(100000);
        builder.withSocketOptions(socketOptions);
        Cluster cluster = builder.build();
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());

        Session session = cluster.connect();

        for (int i = 0; i < 1; i++) {
            String id = "" + i;
            String names = "name" + i;
            String sql = "insert into test.t (id,age,names) values(" + id + "," + i + ",['" + names + "'])";
            session.execute(sql);
            System.out.printf("row inserted, number %d\n", i);
        }
        cluster.close();
    }
}