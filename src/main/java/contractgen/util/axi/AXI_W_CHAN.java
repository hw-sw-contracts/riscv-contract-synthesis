package contractgen.util.axi;

import contractgen.util.StringUtils;

import static contractgen.util.axi.AXI_CONFIG.*;

/**
 * The W channel of the AXI interface.
 */
@SuppressWarnings("MissingJavadoc")
public class AXI_W_CHAN {

    private final long data;
    private final int strb;
    private final int last;
    private final int user;

    public AXI_W_CHAN(long data, int strb, int last, int user) {
        this.data = data;
        this.strb = strb;
        this.last = last;
        this.user = user;
    }

    public static AXI_W_CHAN fromString(String binary) {
        binary = StringUtils.expandToLength(binary, AXI_W_WIDTH, '0');
        return new AXI_W_CHAN(
                Long.parseLong(binary.substring(0, AXI_DATA_WIDTH), 2),
                Integer.parseInt(binary.substring(AXI_DATA_WIDTH, AXI_DATA_WIDTH + AXI_STRB_WIDTH), 2),
                Integer.parseInt(binary.substring(AXI_DATA_WIDTH + AXI_STRB_WIDTH, AXI_DATA_WIDTH + AXI_STRB_WIDTH + AXI_LAST_WIDTH), 2),
                Integer.parseInt(binary.substring(AXI_DATA_WIDTH + AXI_STRB_WIDTH + AXI_LAST_WIDTH, AXI_DATA_WIDTH + AXI_STRB_WIDTH + AXI_LAST_WIDTH + AXI_USER_WIDTH))
        );
    }

    @Override
    public String toString() {
        return "AXIWChan{" +
                "data=" + data +
                ", strb=" + strb +
                ", last=" + last +
                ", user=" + user +
                '}';
    }
}
