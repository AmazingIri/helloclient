import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;


public class IgnitePutAndGet {

    public static void main(String[] args) {


        Ignite ignite = Ignition.start("/Users/zhiqi/apache-ignite-fabric-2.6.0-bin/examples/config/example-cache.xml");
        IgniteCache<Integer, String> cache = ignite.getOrCreateCache("myCacheName");

        // Store keys in cache (values will end up on different cache nodes).
        for (int i = 0; i < 10; i++)
            cache.put(i, Integer.toString(i+2));

        for (int i = 0; i < 10; i++)
            System.out.println("Got [key=" + i + ", val=" + cache.get(i) + ']');

    }
}