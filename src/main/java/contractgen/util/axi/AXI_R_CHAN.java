package contractgen.util.axi;

import contractgen.util.StringUtils;

import static contractgen.util.axi.AXI_CONFIG.*;

/**
 * The R channel of the AXI interface.
 */
@SuppressWarnings("MissingJavadoc")
public class AXI_R_CHAN {

    private final int id;
    private final long data;
    private final int resp;
    private final int last;
    private final int user;

    public AXI_R_CHAN(int id, long data, int resp, int last, int user) {
        this.id = id;
        this.data = data;
        this.resp = resp;
        this.last = last;
        this.user = user;
    }

    public static AXI_R_CHAN fromString(String binary) {
        binary = StringUtils.expandToLength(binary, AXI_R_WIDTH, '0');
        return new AXI_R_CHAN(
                Integer.parseInt(binary.substring(0, AXI_ID_WIDTH), 2),
                Long.parseLong(binary.substring(AXI_ID_WIDTH, AXI_ID_WIDTH + AXI_DATA_WIDTH), 2),
                Integer.parseInt(binary.substring(AXI_ID_WIDTH + AXI_DATA_WIDTH, AXI_ID_WIDTH + AXI_DATA_WIDTH + AXI_RESP_WIDTH), 2),
                Integer.parseInt(binary.substring(AXI_ID_WIDTH + AXI_DATA_WIDTH + AXI_RESP_WIDTH, AXI_ID_WIDTH + AXI_DATA_WIDTH + AXI_RESP_WIDTH + AXI_LAST_WIDTH), 2),
                Integer.parseInt(binary.substring(AXI_ID_WIDTH + AXI_DATA_WIDTH + AXI_RESP_WIDTH + AXI_LAST_WIDTH, AXI_ID_WIDTH + AXI_DATA_WIDTH + AXI_RESP_WIDTH + AXI_LAST_WIDTH + AXI_USER_WIDTH))
        );
    }

    @Override
    public String toString() {
        return "AXIRChan{" +
                "id=" + id +
                ", data=" + data +
                ", resp=" + resp +
                ", last=" + last +
                ", user=" + user +
                '}';
    }
}
