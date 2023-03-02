package contractgen.util.axi;

import contractgen.util.StringUtils;

import static contractgen.util.axi.AXI_CONFIG.*;

public class AXI_RSP {

    private final int aw_ready;
    private final int ar_ready;
    private final int w_ready;
    private final int b_valid;
    private final AXI_B_CHAN b;
    private final int r_valid;
    private final AXI_R_CHAN r;

    public AXI_RSP(int aw_ready, int ar_ready, int w_ready, int b_valid, AXI_B_CHAN b, int r_valid, AXI_R_CHAN r) {
        this.aw_ready = aw_ready;
        this.ar_ready = ar_ready;
        this.w_ready = w_ready;
        this.b_valid = b_valid;
        this.b = b;
        this.r_valid = r_valid;
        this.r = r;
    }

    public static AXI_RSP fromString(String binary) {
        binary = StringUtils.expandToLength(binary, AXI_RSP_WIDTH, '0');
        return new AXI_RSP(
                Integer.parseInt(binary.substring(0, AXI_AW_READY_WIDTH)),
                Integer.parseInt(binary.substring(AXI_AW_READY_WIDTH, AXI_AW_READY_WIDTH + AXI_AR_READY_WIDTH)),
                Integer.parseInt(binary.substring(AXI_AW_READY_WIDTH + AXI_AR_READY_WIDTH, AXI_AW_READY_WIDTH + AXI_AR_READY_WIDTH + AXI_W_READY_WIDTH)),
                Integer.parseInt(binary.substring(AXI_AW_READY_WIDTH + AXI_AR_READY_WIDTH + AXI_W_READY_WIDTH, AXI_AW_READY_WIDTH + AXI_AR_READY_WIDTH + AXI_W_READY_WIDTH + AXI_B_VALID_WIDTH)),
                AXI_B_CHAN.fromString(binary.substring(AXI_AW_READY_WIDTH + AXI_AR_READY_WIDTH + AXI_W_READY_WIDTH + AXI_B_VALID_WIDTH, AXI_AW_READY_WIDTH + AXI_AR_READY_WIDTH + AXI_W_READY_WIDTH + AXI_B_VALID_WIDTH + AXI_B_WIDTH)),
                Integer.parseInt(binary.substring(AXI_AW_READY_WIDTH + AXI_AR_READY_WIDTH + AXI_W_READY_WIDTH + AXI_B_VALID_WIDTH + AXI_B_WIDTH, AXI_AW_READY_WIDTH + AXI_AR_READY_WIDTH + AXI_W_READY_WIDTH + AXI_B_VALID_WIDTH + AXI_B_WIDTH + AXI_R_VALID_WIDTH)),
                AXI_R_CHAN.fromString(binary.substring(AXI_AW_READY_WIDTH + AXI_AR_READY_WIDTH + AXI_W_READY_WIDTH + AXI_B_VALID_WIDTH + AXI_B_WIDTH + AXI_R_VALID_WIDTH, AXI_AW_READY_WIDTH + AXI_AR_READY_WIDTH + AXI_W_READY_WIDTH + AXI_B_VALID_WIDTH + AXI_B_WIDTH + AXI_R_VALID_WIDTH + AXI_R_WIDTH))
        );

    }

    @Override
    public String toString() {
        return "AXI_RSP{" +
                "aw_ready=" + aw_ready +
                ", ar_ready=" + ar_ready +
                ", w_ready=" + w_ready +
                ", b_valid=" + b_valid +
                ", b=" + b +
                ", r_valid=" + r_valid +
                ", r=" + r +
                '}';
    }
}
