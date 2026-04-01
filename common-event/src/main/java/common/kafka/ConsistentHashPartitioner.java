package common.kafka;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 컨시스턴트 해시 파티셔너
 * 파티션 수 변경 시 약 1/N 키만 재배치 (일반 해시는 거의 전체 재배치)
 */
public class ConsistentHashPartitioner implements Partitioner {

    private static final int VIRTUAL_NODES = 100;

    private final ConcurrentHashMap<String, TreeMap<Integer, Integer>> ringCache = new ConcurrentHashMap<>();

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        int numPartitions = cluster.partitionCountForTopic(topic);
        String cacheKey = topic + ":" + numPartitions;

        TreeMap<Integer, Integer> ring = ringCache.computeIfAbsent(cacheKey, k -> buildRing(numPartitions));

        int keyHash = (key.toString().hashCode() & 0x7FFFFFFF);
        Map.Entry<Integer, Integer> entry = ring.ceilingEntry(keyHash);
        return entry != null ? entry.getValue() : ring.firstEntry().getValue();
    }

    private TreeMap<Integer, Integer> buildRing(int numPartitions) {
        TreeMap<Integer, Integer> ring = new TreeMap<>();
        StringBuilder sb = new StringBuilder();

        for (int partition = 0; partition < numPartitions; partition++) {
            for (int i = 0; i < VIRTUAL_NODES; i++) {
                sb.setLength(0);
                sb.append(partition).append("-").append(i);
                int hash = (sb.toString().hashCode() & 0x7FFFFFFF);
                ring.put(hash, partition);
            }
        }
        return ring;
    }

    @Override
    public void close() {}

    @Override
    public void configure(Map<String, ?> configs) {}
}

