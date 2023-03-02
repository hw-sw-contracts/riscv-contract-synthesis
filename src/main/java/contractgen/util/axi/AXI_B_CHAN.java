package contractgen.util.axi;

import contractgen.util.StringUtils;

import static contractgen.util.axi.AXI_CONFIG.*;

public class AXI_B_CHAN {

    private final int id;
    private final int resp;
    private final int user;

    public AXI_B_CHAN(int id, int resp, int user) {
        this.id = id;
        this.resp = resp;
        this.user = user;
    }

    public static AXI_B_CHAN fromString(String binary) {
        binary = StringUtils.expandToLength(binary, AXI_B_WIDTH, '0');
        return new AXI_B_CHAN(
                Integer.parseInt(binary.substring(0, AXI_ID_WIDTH), 2),
                Integer.parseInt(binary.substring(AXI_ID_WIDTH, AXI_ID_WIDTH + AXI_RESP_WIDTH), 2),
                Integer.parseInt(binary.substring(AXI_ID_WIDTH + AXI_RESP_WIDTH, AXI_ID_WIDTH + AXI_RESP_WIDTH + AXI_USER_WIDTH), 2)
        );
    }

    @Override
    public String toString() {
        return "AXIBChan{" +
                "id=" + id +
                ", resp=" + resp +
                ", user=" + user +
                '}';
    }
}
