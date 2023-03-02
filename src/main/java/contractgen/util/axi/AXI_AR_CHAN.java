package contractgen.util.axi;

import contractgen.util.StringUtils;

import static contractgen.util.axi.AXI_CONFIG.*;

public class AXI_AR_CHAN {

    private final int id;
    private final long addr;
    private final int len;
    private final int size;
    private final int burst;
    private final int lock;
    private final int cache;
    private final int prot;
    private final int qos;
    private final int region;
    private final int user;

    public AXI_AR_CHAN(int id, long addr, int len, int size, int burst, int lock, int cache, int prot, int qos, int region, int user) {
        this.id = id;
        this.addr = addr;
        this.len = len;
        this.size = size;
        this.burst = burst;
        this.lock = lock;
        this.cache = cache;
        this.prot = prot;
        this.qos = qos;
        this.region = region;
        this.user = user;
    }

    public static AXI_AR_CHAN fromString(String binary) {
        binary = StringUtils.expandToLength(binary, AXI_AR_WIDTH, '0');
        return new AXI_AR_CHAN(
                Integer.parseInt(binary.substring(0, AXI_ID_WIDTH), 2),
                Long.parseLong(binary.substring(AXI_ID_WIDTH, AXI_ID_WIDTH + AXI_ADDR_WIDTH), 2),
                Integer.parseInt(binary.substring(AXI_ID_WIDTH + AXI_ADDR_WIDTH, AXI_ID_WIDTH + AXI_ADDR_WIDTH + AXI_LEN_WIDTH), 2),
                Integer.parseInt(binary.substring(AXI_ID_WIDTH + AXI_ADDR_WIDTH + AXI_LEN_WIDTH, AXI_ID_WIDTH + AXI_ADDR_WIDTH + AXI_LEN_WIDTH + AXI_SIZE_WIDTH), 2),
                Integer.parseInt(binary.substring(AXI_ID_WIDTH + AXI_ADDR_WIDTH + AXI_LEN_WIDTH + AXI_SIZE_WIDTH, AXI_ID_WIDTH + AXI_ADDR_WIDTH + AXI_LEN_WIDTH + AXI_SIZE_WIDTH + AXI_BURST_WIDTH), 2),
                Integer.parseInt(binary.substring(AXI_ID_WIDTH + AXI_ADDR_WIDTH + AXI_LEN_WIDTH + AXI_SIZE_WIDTH + AXI_BURST_WIDTH, AXI_ID_WIDTH + AXI_ADDR_WIDTH + AXI_LEN_WIDTH + AXI_SIZE_WIDTH + AXI_BURST_WIDTH + AXI_LOCK_WIDTH), 2),
                Integer.parseInt(binary.substring(AXI_ID_WIDTH + AXI_ADDR_WIDTH + AXI_LEN_WIDTH + AXI_SIZE_WIDTH + AXI_BURST_WIDTH + AXI_LOCK_WIDTH, AXI_ID_WIDTH + AXI_ADDR_WIDTH + AXI_LEN_WIDTH + AXI_SIZE_WIDTH + AXI_BURST_WIDTH + AXI_LOCK_WIDTH + AXI_CACHE_WIDTH), 2),
                Integer.parseInt(binary.substring(AXI_ID_WIDTH + AXI_ADDR_WIDTH + AXI_LEN_WIDTH + AXI_SIZE_WIDTH + AXI_BURST_WIDTH + AXI_LOCK_WIDTH + AXI_CACHE_WIDTH, AXI_ID_WIDTH + AXI_ADDR_WIDTH + AXI_LEN_WIDTH + AXI_SIZE_WIDTH + AXI_BURST_WIDTH + AXI_LOCK_WIDTH + AXI_CACHE_WIDTH + AXI_PROT_WIDTH), 2),
                Integer.parseInt(binary.substring(AXI_ID_WIDTH + AXI_ADDR_WIDTH + AXI_LEN_WIDTH + AXI_SIZE_WIDTH + AXI_BURST_WIDTH + AXI_LOCK_WIDTH + AXI_CACHE_WIDTH + AXI_PROT_WIDTH, AXI_ID_WIDTH + AXI_ADDR_WIDTH + AXI_LEN_WIDTH + AXI_SIZE_WIDTH + AXI_BURST_WIDTH + AXI_LOCK_WIDTH + AXI_CACHE_WIDTH + AXI_PROT_WIDTH + AXI_QOS_WIDTH), 2),
                Integer.parseInt(binary.substring(AXI_ID_WIDTH + AXI_ADDR_WIDTH + AXI_LEN_WIDTH + AXI_SIZE_WIDTH + AXI_BURST_WIDTH + AXI_LOCK_WIDTH + AXI_CACHE_WIDTH + AXI_PROT_WIDTH + AXI_QOS_WIDTH, AXI_ID_WIDTH + AXI_ADDR_WIDTH + AXI_LEN_WIDTH + AXI_SIZE_WIDTH + AXI_BURST_WIDTH + AXI_LOCK_WIDTH + AXI_CACHE_WIDTH + AXI_PROT_WIDTH + AXI_QOS_WIDTH + AXI_REGION_WIDTH), 2),
                Integer.parseInt(binary.substring(AXI_ID_WIDTH + AXI_ADDR_WIDTH + AXI_LEN_WIDTH + AXI_SIZE_WIDTH + AXI_BURST_WIDTH + AXI_LOCK_WIDTH + AXI_CACHE_WIDTH + AXI_PROT_WIDTH + AXI_QOS_WIDTH + AXI_REGION_WIDTH, AXI_ID_WIDTH + AXI_ADDR_WIDTH + AXI_LEN_WIDTH + AXI_SIZE_WIDTH + AXI_BURST_WIDTH + AXI_LOCK_WIDTH + AXI_CACHE_WIDTH + AXI_PROT_WIDTH + AXI_QOS_WIDTH + AXI_REGION_WIDTH + AXI_USER_WIDTH), 2)
        );
    }

    @Override
    public String toString() {
        return "AXIARChan{" +
                "id=" + id +
                ", addr=" + addr +
                ", len=" + len +
                ", size=" + size +
                ", burst=" + burst +
                ", lock=" + lock +
                ", cache=" + cache +
                ", prot=" + prot +
                ", qos=" + qos +
                ", region=" + region +
                ", user=" + user +
                '}';
    }
}
