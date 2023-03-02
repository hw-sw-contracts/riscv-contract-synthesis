package contractgen.util.axi;

public class AXI_CONFIG {

    public static final int AXI_ADDR_WIDTH = 64;
    public static final int AXI_DATA_WIDTH = 64;
    public static final int AXI_ID_WIDTH = 4;
    public static final int AXI_STRB_WIDTH = 8;
    public static final int AXI_USER_WIDTH = 2;

    public static final int AXI_LEN_WIDTH = 8;
    public static final int AXI_SIZE_WIDTH = 3;
    public static final int AXI_BURST_WIDTH = 2;
    public static final int AXI_LOCK_WIDTH = 1;
    public static final int AXI_CACHE_WIDTH = 4;
    public static final int AXI_PROT_WIDTH = 3;
    public static final int AXI_QOS_WIDTH = 4;
    public static final int AXI_REGION_WIDTH = 4;
    public static final int AXI_ATOP_WIDTH = 6;

    public static final int AXI_LAST_WIDTH = 1;

    public static final int AXI_RESP_WIDTH = 2;

    public static final int AXI_AW_VALID_WIDTH = 1;
    public static final int AXI_W_VALID_WIDTH = 1;
    public static final int AXI_AR_VALID_WIDTH = 1;
    public static final int AXI_B_VALID_WIDTH = 1;
    public static final int AXI_R_VALID_WIDTH = 1;
    public static final int AXI_AW_READY_WIDTH = 1;
    public static final int AXI_W_READY_WIDTH = 1;
    public static final int AXI_AR_READY_WIDTH = 1;
    public static final int AXI_B_READY_WIDTH = 1;
    public static final int AXI_R_READY_WIDTH = 1;


    public static final int AXI_AW_WIDTH =
            AXI_ID_WIDTH +
                    AXI_ADDR_WIDTH +
                    AXI_LEN_WIDTH +
                    AXI_SIZE_WIDTH +
                    AXI_BURST_WIDTH +
                    AXI_LOCK_WIDTH +
                    AXI_CACHE_WIDTH +
                    AXI_PROT_WIDTH +
                    AXI_QOS_WIDTH +
                    AXI_REGION_WIDTH +
                    AXI_ATOP_WIDTH +
                    AXI_USER_WIDTH;

    public static final int AXI_AR_WIDTH =
            AXI_ID_WIDTH +
                    AXI_ADDR_WIDTH +
                    AXI_LEN_WIDTH +
                    AXI_SIZE_WIDTH +
                    AXI_BURST_WIDTH +
                    AXI_LOCK_WIDTH +
                    AXI_CACHE_WIDTH +
                    AXI_PROT_WIDTH +
                    AXI_QOS_WIDTH +
                    AXI_REGION_WIDTH +
                    AXI_USER_WIDTH;

    public static final int AXI_R_WIDTH =
            AXI_ID_WIDTH +
                    AXI_DATA_WIDTH +
                    AXI_RESP_WIDTH +
                    AXI_LAST_WIDTH +
                    AXI_USER_WIDTH;

    public static final int AXI_W_WIDTH =
            AXI_DATA_WIDTH +
                    AXI_STRB_WIDTH +
                    AXI_LAST_WIDTH +
                    AXI_USER_WIDTH;


    public static final int AXI_B_WIDTH =
            AXI_ID_WIDTH +
                    AXI_RESP_WIDTH +
                    AXI_USER_WIDTH;

    public static final int AXI_REQ_WIDTH =
            AXI_AW_WIDTH +
                    AXI_AW_VALID_WIDTH +
                    AXI_W_WIDTH +
                    AXI_W_VALID_WIDTH +
                    AXI_B_READY_WIDTH +
                    AXI_AR_WIDTH +
                    AXI_AR_VALID_WIDTH +
                    AXI_R_READY_WIDTH;

    public static final int AXI_RSP_WIDTH =
            AXI_AW_READY_WIDTH +
                    AXI_AR_READY_WIDTH +
                    AXI_W_READY_WIDTH +
                    AXI_B_VALID_WIDTH +
                    AXI_B_WIDTH +
                    AXI_R_VALID_WIDTH +
                    AXI_R_WIDTH;



}
