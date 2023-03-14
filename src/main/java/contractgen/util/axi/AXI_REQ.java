package contractgen.util.axi;

import contractgen.util.StringUtils;

import static contractgen.util.axi.AXI_CONFIG.*;

/**
 * The request type of the AXI interface.
 */
@SuppressWarnings("MissingJavadoc")
public class AXI_REQ {

    private final AXI_AW_CHAN aw;
    private final int aw_valid;
    private final AXI_W_CHAN w;
    private final int w_valid;
    private final int b_ready;
    private final AXI_AR_CHAN ar;
    private final int ar_valid;
    private final int r_ready;

    public AXI_REQ(AXI_AW_CHAN aw, int aw_valid, AXI_W_CHAN w, int w_valid, int b_ready, AXI_AR_CHAN ar, int ar_valid, int r_ready) {
        this.aw = aw;
        this.aw_valid = aw_valid;
        this.w = w;
        this.w_valid = w_valid;
        this.b_ready = b_ready;
        this.ar = ar;
        this.ar_valid = ar_valid;
        this.r_ready = r_ready;
    }

    public static AXI_REQ fromString(String binary) {
        binary = StringUtils.expandToLength(binary, AXI_REQ_WIDTH, '0');
        return new AXI_REQ(
                AXI_AW_CHAN.fromString(binary.substring(0, AXI_AW_WIDTH)),
                Integer.parseInt(binary.substring(AXI_AW_WIDTH, AXI_AW_WIDTH + AXI_AW_VALID_WIDTH)),
                AXI_W_CHAN.fromString(binary.substring(AXI_AW_WIDTH + AXI_AW_VALID_WIDTH, AXI_AW_WIDTH + AXI_AW_VALID_WIDTH + AXI_W_WIDTH)),
                Integer.parseInt(binary.substring(AXI_AW_WIDTH + AXI_AW_VALID_WIDTH + AXI_W_WIDTH, AXI_AW_WIDTH + AXI_AW_VALID_WIDTH + AXI_W_WIDTH + AXI_W_VALID_WIDTH), 2),
                Integer.parseInt(binary.substring(AXI_AW_WIDTH + AXI_AW_VALID_WIDTH + AXI_W_WIDTH + AXI_W_VALID_WIDTH, AXI_AW_WIDTH + AXI_AW_VALID_WIDTH + AXI_W_WIDTH + AXI_W_VALID_WIDTH + AXI_B_READY_WIDTH), 2),
                AXI_AR_CHAN.fromString(binary.substring(AXI_AW_WIDTH + AXI_AW_VALID_WIDTH + AXI_W_WIDTH + AXI_W_VALID_WIDTH + AXI_B_READY_WIDTH, AXI_AW_WIDTH + AXI_AW_VALID_WIDTH + AXI_W_WIDTH + AXI_W_VALID_WIDTH + AXI_B_READY_WIDTH + AXI_AR_WIDTH)),
                Integer.parseInt(binary.substring(AXI_AW_WIDTH + AXI_AW_VALID_WIDTH + AXI_W_WIDTH + AXI_W_VALID_WIDTH + AXI_B_READY_WIDTH + AXI_AR_WIDTH, AXI_AW_WIDTH + AXI_AW_VALID_WIDTH + AXI_W_WIDTH + AXI_W_VALID_WIDTH + AXI_B_READY_WIDTH + AXI_AR_WIDTH + AXI_AR_VALID_WIDTH), 2),
                Integer.parseInt(binary.substring(AXI_AW_WIDTH + AXI_AW_VALID_WIDTH + AXI_W_WIDTH + AXI_W_VALID_WIDTH + AXI_B_READY_WIDTH + AXI_AR_WIDTH + AXI_AR_VALID_WIDTH, AXI_AW_WIDTH + AXI_AW_VALID_WIDTH + AXI_W_WIDTH + AXI_W_VALID_WIDTH + AXI_B_READY_WIDTH + AXI_AR_WIDTH + AXI_AR_VALID_WIDTH + AXI_R_READY_WIDTH), 2)
        );
    }

    @Override
    public String toString() {
        return "AXIRequest{" +
                "aw=" + aw +
                ", aw_valid=" + aw_valid +
                ", w=" + w +
                ", w_valid=" + w_valid +
                ", b_ready=" + b_ready +
                ", ar=" + ar +
                ", ar_valid=" + ar_valid +
                ", r_ready=" + r_ready +
                '}';
    }
}
