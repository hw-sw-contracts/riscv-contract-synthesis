import ariane_pkg::*;
import ariane_rvfi_pkg::*;


/// An AXI4 interface.
interface AXI_BUS #(
  parameter AXI_ADDR_WIDTH = -1,
  parameter AXI_DATA_WIDTH = -1,
  parameter AXI_ID_WIDTH   = -1,
  parameter AXI_USER_WIDTH = -1
);

  import axi_pkg::*;

  localparam AXI_STRB_WIDTH = AXI_DATA_WIDTH / 8;

  typedef logic [AXI_ID_WIDTH-1:0]   id_t;
  typedef logic [AXI_ADDR_WIDTH-1:0] addr_t;
  typedef logic [AXI_DATA_WIDTH-1:0] data_t;
  typedef logic [AXI_STRB_WIDTH-1:0] strb_t;
  typedef logic [AXI_USER_WIDTH-1:0] user_t;
  typedef logic [5:0] atop_t;

  id_t        aw_id;
  addr_t      aw_addr;
  logic [7:0] aw_len;
  logic [2:0] aw_size;
  burst_t     aw_burst;
  logic       aw_lock;
  cache_t     aw_cache;
  prot_t      aw_prot;
  qos_t       aw_qos;
  atop_t      aw_atop;
  region_t    aw_region;
  user_t      aw_user;
  logic       aw_valid;
  logic       aw_ready;

  data_t      w_data;
  strb_t      w_strb;
  logic       w_last;
  user_t      w_user;
  logic       w_valid;
  logic       w_ready;

  id_t        b_id;
  resp_t      b_resp;
  user_t      b_user;
  logic       b_valid;
  logic       b_ready;

  id_t        ar_id;
  addr_t      ar_addr;
  logic [7:0] ar_len;
  logic [2:0] ar_size;
  burst_t     ar_burst;
  logic       ar_lock;
  cache_t     ar_cache;
  prot_t      ar_prot;
  qos_t       ar_qos;
  region_t    ar_region;
  user_t      ar_user;
  logic       ar_valid;
  logic       ar_ready;

  id_t        r_id;
  data_t      r_data;
  resp_t      r_resp;
  logic       r_last;
  user_t      r_user;
  logic       r_valid;
  logic       r_ready;

  modport Master (
    output aw_id, aw_addr, aw_len, aw_size, aw_burst, aw_lock, aw_cache, aw_prot, aw_qos, aw_atop, aw_region, aw_user, aw_valid, input aw_ready,
    output w_data, w_strb, w_last, w_user, w_valid, input w_ready,
    input b_id, b_resp, b_user, b_valid, output b_ready,
    output ar_id, ar_addr, ar_len, ar_size, ar_burst, ar_lock, ar_cache, ar_prot, ar_qos, ar_region, ar_user, ar_valid, input ar_ready,
    input r_id, r_data, r_resp, r_last, r_user, r_valid, output r_ready
  );

  modport Slave (
    input aw_id, aw_addr, aw_len, aw_size, aw_burst, aw_lock, aw_cache, aw_prot, aw_qos, aw_atop, aw_region, aw_user, aw_valid, output aw_ready,
    input w_data, w_strb, w_last, w_user, w_valid, output w_ready,
    output b_id, b_resp, b_user, b_valid, input b_ready,
    input ar_id, ar_addr, ar_len, ar_size, ar_burst, ar_lock, ar_cache, ar_prot, ar_qos, ar_region, ar_user, ar_valid, output ar_ready,
    output r_id, r_data, r_resp, r_last, r_user, r_valid, input r_ready
  );

endinterface

module top (

);
    (* gclk *) reg clk;

    logic clock;
    initial clock = 0;
    always @(posedge clk) begin
        clock <= !clock;
    end

    logic clock_1;
    logic clock_2;

    logic reset_1;
    logic reset_2;
	initial begin
		reset_1 <= 0;
		reset_2 <= 0;
	end
	always @(posedge clock) begin
		reset_1 <= 1;
		reset_2 <= 1;
    end

    
	integer counter;
	initial counter <= 0;
	always @(posedge clock) begin
		counter <= counter +1;
	end


    logic req_1;
    logic req_2;
    logic [63:0] addr_1;
    logic [63:0] addr_2;
    logic [3:0] be_1;
    logic [3:0] be_2;
    logic [63:0] data_w_1;
    logic [63:0] data_w_2;
    logic [63:0] data_r_1;
    logic [63:0] data_r_2;


    logic retire_1 = 1;
    logic retire_2 = 1;
    logic [31:0] retire_instr_1;
    logic [31:0] retire_instr_2;
    logic fetch_1;
    logic fetch_2;
    logic [31:0] reg_rs1_1;
    logic [31:0] reg_rs1_2;
    logic [31:0] reg_rs2_1;
    logic [31:0] reg_rs2_2;
    logic [31:0] reg_rd_1;
    logic [31:0] reg_rd_2;
    logic [31:0] mem_addr_1;
    logic [31:0] mem_addr_2;
    logic [31:0] mem_r_data_1;
    logic [31:0] mem_r_data_2;
    logic [31:0] mem_w_data_1;
    logic [31:0] mem_w_data_2;


    logic retire;
    logic atk_equiv;
    logic ctr_equiv;

    logic enable_1;
    logic enable_2;
    logic finished;

`ifdef RVFI_TRACE
    rvfi_instr_t [1:0] rvfi_1;

    logic valid_1_1;
    logic valid_1_2;
    logic [31:0] insn_1_1;
    logic [31:0] insn_1_2;
    logic [31:0] rs1_rdata_1_1;
    logic [31:0] rs1_rdata_1_2;
    logic [31:0] rs2_rdata_1_1;
    logic [31:0] rs2_rdata_1_2;
    logic [31:0] rd_wdata_1_1;
    logic [31:0] rd_wdata_1_2;
    logic [31:0] mem_addr_1_1;
    logic [31:0] mem_addr_1_2;
    logic [31:0] mem_rdata_1_1;
    logic [31:0] mem_rdata_1_2;
    logic [31:0] mem_wdata_1_1;
    logic [31:0] mem_wdata_1_2;

    rvfi_unwrap rvfi_unwrap_1_1 (
        .rvfi_instr_i (rvfi_1[0]),
        .valid_o(valid_1_1),
        .insn_o(insn_1_1),
        .rs1_rdata_o(rs1_rdata_1_1),
        .rs2_rdata_o(rs2_rdata_1_1),
        .rd_wdata_o(rd_wdata_1_1),
        .mem_addr_o(mem_addr_1_1),
        .mem_rdata_o(mem_rdata_1_1),
        .mem_wdata_o(mem_wdata_1_1),
    );

    rvfi_unwrap rvfi_unwrap_1_2 (
        .rvfi_instr_i (rvfi_1[1]),
        .valid_o(valid_1_2),
        .insn_o(insn_1_2),
        .rs1_rdata_o(rs1_rdata_1_2),
        .rs2_rdata_o(rs2_rdata_1_2),
        .rd_wdata_o(rd_wdata_1_2),
        .mem_addr_o(mem_addr_1_2),
        .mem_rdata_o(mem_rdata_1_2),
        .mem_wdata_o(mem_wdata_1_2),
    );

    always @(clock_1) retire_1 <= valid_1_1 || valid_1_2;
    always @(clock_1) retire_instr_1 <= valid_1_1 == 1'b1 ? insn_1_1 : insn_1_2;
    always @(clock_1) reg_rs1_1 <= valid_1_1 == 1'b1 ? rs1_rdata_1_1 : rs1_rdata_1_2;
    always @(clock_1) reg_rs2_1 <= valid_1_1 == 1'b1 ? rs2_rdata_1_1 : rs2_rdata_1_2;
    always @(clock_1) reg_rd_1 <= valid_1_1 == 1'b1 ? rd_wdata_1_1 : rd_wdata_1_2;
    always @(clock_1) mem_addr_1 <= valid_1_1 == 1'b1 ? mem_addr_1_1 : mem_addr_1_2;
    always @(clock_1) mem_r_data_1 <= valid_1_1 == 1'b1 ? mem_rdata_1_1 : mem_rdata_1_2;
    always @(clock_1) mem_w_data_1 <= valid_1_1 == 1'b1 ? mem_wdata_1_1 : mem_wdata_1_2;


    rvfi_instr_t [1:0] rvfi_2;

    logic valid_2_1;
    logic valid_2_2;
    logic [31:0] insn_2_1;
    logic [31:0] insn_2_2;
    logic [31:0] rs1_rdata_2_1;
    logic [31:0] rs1_rdata_2_2;
    logic [31:0] rs2_rdata_2_1;
    logic [31:0] rs2_rdata_2_2;
    logic [31:0] rd_wdata_2_1;
    logic [31:0] rd_wdata_2_2;
    logic [31:0] mem_addr_2_1;
    logic [31:0] mem_addr_2_2;
    logic [31:0] mem_rdata_2_1;
    logic [31:0] mem_rdata_2_2;
    logic [31:0] mem_wdata_2_1;
    logic [31:0] mem_wdata_2_2;

    rvfi_unwrap rvfi_unwrap_2_1 (
        .rvfi_instr_i (rvfi_2[0]),
        .valid_o(valid_2_1),
        .insn_o(insn_2_1),
        .rs1_rdata_o(rs1_rdata_2_1),
        .rs2_rdata_o(rs2_rdata_2_1),
        .rd_wdata_o(rd_wdata_2_1),
        .mem_addr_o(mem_addr_2_1),
        .mem_rdata_o(mem_rdata_2_1),
        .mem_wdata_o(mem_wdata_2_1),
    );

    rvfi_unwrap rvfi_unwrap_2_2 (
        .rvfi_instr_i (rvfi_2[1]),
        .valid_o(valid_2_2),
        .insn_o(insn_2_2),
        .rs1_rdata_o(rs1_rdata_2_2),
        .rs2_rdata_o(rs2_rdata_2_2),
        .rd_wdata_o(rd_wdata_2_2),
        .mem_addr_o(mem_addr_2_2),
        .mem_rdata_o(mem_rdata_2_2),
        .mem_wdata_o(mem_wdata_2_2),
    );

    always @(clock_1) retire_2 <= valid_2_1 || valid_2_2;
    always @(clock_1) retire_instr_2 <= valid_2_1 == 1'b1 ? insn_2_1 : insn_2_2;
    always @(clock_1) reg_rs1_2 <= valid_2_1 == 1'b1 ? rs1_rdata_2_1 : rs1_rdata_2_2;
    always @(clock_1) reg_rs2_2 <= valid_2_1 == 1'b1 ? rs2_rdata_2_1 : rs2_rdata_2_2;
    always @(clock_1) reg_rd_2 <= valid_2_1 == 1'b1 ? rd_wdata_2_1 : rd_wdata_2_2;
    always @(clock_1) mem_addr_2 <= valid_2_1 == 1'b1 ? mem_addr_2_1 : mem_addr_2_2;
    always @(clock_1) mem_r_data_2 <= valid_2_1 == 1'b1 ? mem_rdata_2_1 : mem_rdata_2_2;
    always @(clock_1) mem_w_data_2 <= valid_2_1 == 1'b1 ? mem_wdata_2_1 : mem_wdata_2_2;
`endif

    mem #(
        .ID                     (1),
    ) mem_1 (
        .clk_i                  (clock_1),
        .enable_i               (enable_1),
        .req_i                  (req_1),
        .we_i                   (we_1),
        .addr_i                 (addr_1),
        .be_i                   (be_1),
        .data_i                 (data_w_1),
        .data_o                 (data_r_1),
    );

    mem #(
        .ID                     (2),
    ) mem_2 (
        .clk_i                  (clock_2),
        .enable_i               (enable_2),
        .req_i                  (req_2),
        .we_i                   (we_2),
        .addr_i                 (addr_2),
        .be_i                   (be_2),
        .data_i                 (data_w_2),
        .data_o                 (data_r_2),
    );

    ariane_axi::req_t axi_req_1;
    ariane_axi::resp_t axi_resp_1;
    ariane_axi::req_t axi_req_2;
    ariane_axi::resp_t axi_resp_2;

    AXI_BUS #(
        .AXI_ADDR_WIDTH         (64), 
        .AXI_DATA_WIDTH         (64), 
        .AXI_ID_WIDTH           (4), 
        .AXI_USER_WIDTH         (2),
    ) axi_bus_1 ();

    axi_converter axi_converter_1 (
        .axi_req_i              (axi_req_1),
        .axi_resp_o             (axi_resp_1),
        .master                 (axi_bus_1.Master),
    );

    AXI_BUS #(
        .AXI_ADDR_WIDTH         (64), 
        .AXI_DATA_WIDTH         (64), 
        .AXI_ID_WIDTH           (4), 
        .AXI_USER_WIDTH         (2),
    ) axi_bus_2 ();

    axi_converter axi_converter_2 (
        .axi_req_i              (axi_req_2),
        .axi_resp_o             (axi_resp_2),
        .master                 (axi_bus_2.Master),
    );

    axi2mem #(
        .AXI_ID_WIDTH           (4),
        .AXI_ADDR_WIDTH         (64),
        .AXI_DATA_WIDTH         (64),
        .AXI_USER_WIDTH         (2),
    ) axi2mem_1 (
        .clk_i                  (clock_1),
        .rst_ni                 (reset_1),
        .slave                  (axi_bus_1.Slave),
        .req_o                  (req_1),
        .we_o                   (we_1),
        .addr_o                 (addr_1),
        .be_o                   (be_1),
        .data_o                 (data_w_1),
        .data_i                 (data_r_1),
    );

    axi2mem #(
        .AXI_ID_WIDTH           (4),
        .AXI_ADDR_WIDTH         (64),
        .AXI_DATA_WIDTH         (64),
        .AXI_USER_WIDTH         (2),
    ) axi2mem_2 (
        .clk_i                  (clock_2),
        .rst_ni                 (reset_2),
        .slave                  (axi_bus_2.Slave),
        .req_o                  (req_2),
        .we_o                   (we_2),
        .addr_o                 (addr_2),
        .be_o                   (be_2),
        .data_o                 (data_w_2),
        .data_i                 (data_r_2),
    );

    ariane #(
        .ArianeCfg              (ariane_pkg::ArianeDefaultConfig),
    ) core_1 (
        .clk_i                  (clock_1),
        .rst_ni                 (reset_1),
        .boot_addr_i            (32'h1000), // TODO
        .hart_id_i              (32'h0),
        .irq_i                  (2'b0),
        .ipi_i                  (1'b0),
        .time_irq_i             (1'b0),
        .debug_req_i            (1'b0),
    `ifdef RVFI_TRACE
        .rvfi_o                 (rvfi_1),
    `endif
        .axi_req_o              (axi_req_1),
        .axi_resp_i             (axi_resp_1),
        .enable_issue_i         (enable_1),
        .issue_o                (issue_1),
    );

    ariane #(
        .ArianeCfg              (ariane_pkg::ArianeDefaultConfig),
    ) core_2 (
        .clk_i                  (clock_2),
        .rst_ni                 (reset_2),
        .boot_addr_i            (32'h1000), // TODO
        .hart_id_i              (32'h0),
        .irq_i                  (2'b0),
        .ipi_i                  (1'b0),
        .time_irq_i             (1'b0),
        .debug_req_i            (1'b0),
    `ifdef RVFI_TRACE
        .rvfi_o                 (rvfi_2),
    `endif
        .axi_req_o              (axi_req_2),
        .axi_resp_i             (axi_resp_2),
        .enable_issue_i         (enable_2),
        .issue_o                (issue_2),
    );

    atk atk (
        .clk_i(clock),
        .atk_observation_1_i    (clock_1),
        .atk_observation_2_i    (clock_2),
        .atk_equiv_o            (atk_equiv),
    );

    ctr ctr (
        .clk_i                  (clock),
        .retire_i               (retire),
        .instr_1_i              (retire_instr_1),
        .instr_2_i              (retire_instr_2),
        .reg_rs1_1              (reg_rs1_1),
        .reg_rs1_2              (reg_rs1_2),
        .reg_rs2_1              (reg_rs2_1),
        .reg_rs2_2              (reg_rs2_2),
        .reg_rd_1               (reg_rd_1),
        .reg_rd_2               (reg_rd_2),
        .mem_addr_1             (mem_addr_1),
        .mem_addr_2             (mem_addr_2),
        .mem_r_data_1           (mem_r_data_1),
        .mem_r_data_2           (mem_r_data_2),
        .mem_w_data_1           (mem_w_data_1),
        .mem_w_data_2           (mem_w_data_2),
        .ctr_equiv_o            (ctr_equiv),
    );

    clk_sync clk_sync (
        .clk_i                  (clock),
        .retire_1_i             (retire_1),
        .retire_2_i             (retire_2),
        .clk_1_o                (clock_1),
        .clk_2_o                (clock_2),
        .retire_o               (retire),
    );

    control control (
        .clk_i                  (clock),
        .retire_i               (retire),
        .fetch_1_i              (issue_1),
        .fetch_2_i              (issue_2),
        .enable_1_o             (enable_1),
        .enable_2_o             (enable_2),
        .finished_o             (finished),
    );

//    always @(posedge clk) begin
//        if (finished && ctr_equiv && !atk_equiv) {
//            $finish();
//        }
//    end

endmodule

module axi_converter (
    input ariane_axi::req_t axi_req_i,
    output ariane_axi::resp_t axi_resp_o,
    AXI_BUS.Master master,
);
    // Request
    assign master.aw_id = axi_req_i.aw.id;
    assign master.aw_addr = axi_req_i.aw.addr;
    assign master.aw_len = axi_req_i.aw.len;
    assign master.aw_size = axi_req_i.aw.size;
    assign master.aw_burst = axi_req_i.aw.burst;
    assign master.aw_lock = axi_req_i.aw.lock;
    assign master.aw_cache = axi_req_i.aw.cache;
    assign master.aw_prot = axi_req_i.aw.prot;
    assign master.aw_qos = axi_req_i.aw.qos;
    assign master.aw_region = axi_req_i.aw.region;
    assign master.aw_atop = axi_req_i.aw.atop;
    assign master.aw_user = axi_req_i.aw.user;
    
    assign master.aw_valid = axi_req_i.aw_valid;

    assign master.w_data = axi_req_i.w.data;
    assign master.w_strb = axi_req_i.w.strb;
    assign master.w_last = axi_req_i.w.last;
    assign master.w_user = axi_req_i.w.user;
    
    assign master.w_valid = axi_req_i.w_valid;
    
    assign master.b_ready = axi_req_i.b_ready;
    
    assign master.ar_id = axi_req_i.ar.id;
    assign master.ar_addr = axi_req_i.ar.addr;
    assign master.ar_len = axi_req_i.ar.len;
    assign master.ar_size = axi_req_i.ar.size;
    assign master.ar_burst = axi_req_i.ar.burst;
    assign master.ar_lock = axi_req_i.ar.lock;
    assign master.ar_cache = axi_req_i.ar.cache;
    assign master.ar_prot = axi_req_i.ar.prot;
    assign master.ar_qos = axi_req_i.ar.qos;
    assign master.ar_region = axi_req_i.ar.region;
    assign master.ar_user = axi_req_i.ar.user;
    
    assign master.ar_valid = axi_req_i.ar_valid;
    
    assign master.r_ready = axi_req_i.r_ready;
    
    // Response
    assign axi_resp_o.aw_ready = master.aw_ready;

    assign axi_resp_o.ar_ready = master.ar_ready;

    assign axi_resp_o.w_ready = master.w_ready;

    assign axi_resp_o.b_valid = master.b_valid;

    assign axi_resp_o.b.id = master.b_id;
    assign axi_resp_o.b.resp = master.b_resp;
    assign axi_resp_o.b.user = master.b_user;
    
    assign axi_resp_o.r_valid = master.r_valid;

    assign axi_resp_o.r.id = master.r_id;
    assign axi_resp_o.r.data = master.r_data;
    assign axi_resp_o.r.resp = master.r_resp;
    assign axi_resp_o.r.last = master.r_last;
    assign axi_resp_o.r.user = master.r_user;
endmodule

module axi2mem #(
    parameter int unsigned AXI_ID_WIDTH      = 10,
    parameter int unsigned AXI_ADDR_WIDTH    = 64,
    parameter int unsigned AXI_DATA_WIDTH    = 64,
    parameter int unsigned AXI_USER_WIDTH    = 10
)(
    input logic                         clk_i,    // Clock
    input logic                         rst_ni,  // Asynchronous reset active low
    AXI_BUS.Slave                       slave,
    output logic                        req_o,
    output logic                        we_o,
    output logic [AXI_ADDR_WIDTH-1:0]   addr_o,
    output logic [AXI_DATA_WIDTH/8-1:0] be_o,
    output logic [AXI_USER_WIDTH-1:0]   user_o,
    output logic [AXI_DATA_WIDTH-1:0]   data_o,
    input  logic [AXI_USER_WIDTH-1:0]   user_i,
    input  logic [AXI_DATA_WIDTH-1:0]   data_i
);

    // AXI has the following rules governing the use of bursts:
    // - for wrapping bursts, the burst length must be 2, 4, 8, or 16
    // - a burst must not cross a 4KB address boundary
    // - early termination of bursts is not supported.
    typedef enum logic [1:0] { FIXED = 2'b00, INCR = 2'b01, WRAP = 2'b10} axi_burst_t;

    localparam LOG_NR_BYTES = $clog2(AXI_DATA_WIDTH/8);

    typedef struct packed {
        logic [AXI_ID_WIDTH-1:0]   id;
        logic [AXI_ADDR_WIDTH-1:0] addr;
        logic [7:0]                len;
        logic [2:0]                size;
        axi_burst_t                burst;
    } ax_req_t;

    // Registers
    enum logic [2:0] { IDLE, READ, WRITE, SEND_B, WAIT_WVALID }  state_d, state_q;
    ax_req_t                   ax_req_d, ax_req_q;
    logic [AXI_ADDR_WIDTH-1:0] req_addr_d, req_addr_q;
    logic [7:0]                cnt_d, cnt_q;

    function automatic logic [AXI_ADDR_WIDTH-1:0] get_wrap_boundary (input logic [AXI_ADDR_WIDTH-1:0] unaligned_address, input logic [7:0] len);
        logic [AXI_ADDR_WIDTH-1:0] warp_address = '0;
        //  for wrapping transfers ax_len can only be of size 1, 3, 7 or 15
        if (len == 4'b1)
            warp_address[AXI_ADDR_WIDTH-1:1+LOG_NR_BYTES] = unaligned_address[AXI_ADDR_WIDTH-1:1+LOG_NR_BYTES];
        else if (len == 4'b11)
            warp_address[AXI_ADDR_WIDTH-1:2+LOG_NR_BYTES] = unaligned_address[AXI_ADDR_WIDTH-1:2+LOG_NR_BYTES];
        else if (len == 4'b111)
            warp_address[AXI_ADDR_WIDTH-1:3+LOG_NR_BYTES] = unaligned_address[AXI_ADDR_WIDTH-3:2+LOG_NR_BYTES];
        else if (len == 4'b1111)
            warp_address[AXI_ADDR_WIDTH-1:4+LOG_NR_BYTES] = unaligned_address[AXI_ADDR_WIDTH-3:4+LOG_NR_BYTES];

        return warp_address;
    endfunction

    logic [AXI_ADDR_WIDTH-1:0] aligned_address;
    logic [AXI_ADDR_WIDTH-1:0] wrap_boundary;
    logic [AXI_ADDR_WIDTH-1:0] upper_wrap_boundary;
    logic [AXI_ADDR_WIDTH-1:0] cons_addr;

    always_comb begin
        // address generation
        aligned_address = {ax_req_q.addr[AXI_ADDR_WIDTH-1:LOG_NR_BYTES], {{LOG_NR_BYTES}{1'b0}}};
        wrap_boundary = get_wrap_boundary(ax_req_q.addr, ax_req_q.len);
        // this will overflow
        upper_wrap_boundary = wrap_boundary + ((ax_req_q.len + 1) << LOG_NR_BYTES);
        // calculate consecutive address
        cons_addr = aligned_address + (cnt_q << LOG_NR_BYTES);

        // Transaction attributes
        // default assignments
        state_d    = state_q;
        ax_req_d   = ax_req_q;
        req_addr_d = req_addr_q;
        cnt_d      = cnt_q;
        // Memory default assignments
        data_o = slave.w_data;
        user_o = slave.w_user;
        be_o   = slave.w_strb;
        we_o   = 1'b0;
        req_o  = 1'b0;
        addr_o = '0;
        // AXI assignments
        // request
        slave.aw_ready = 1'b0;
        slave.ar_ready = 1'b0;
        // read response channel
        slave.r_valid  = 1'b0;
        slave.r_data   = data_i;
        slave.r_resp   = '0;
        slave.r_last   = '0;
        slave.r_id     = ax_req_q.id;
        slave.r_user   = user_i;
        // slave write data channel
        slave.w_ready  = 1'b0;
        // write response channel
        slave.b_valid  = 1'b0;
        slave.b_resp   = 1'b0;
        slave.b_id     = 1'b0;
        slave.b_user   = 1'b0;

        case (state_q)

            IDLE: begin
                // Wait for a read or write
                // ------------
                // Read
                // ------------
                if (slave.ar_valid) begin
                    slave.ar_ready = 1'b1;
                    // sample ax
                    ax_req_d       = {slave.ar_id, slave.ar_addr, slave.ar_len, slave.ar_size, slave.ar_burst};
                    state_d        = READ;
                    //  we can request the first address, this saves us time
                    req_o          = 1'b1;
                    addr_o         = slave.ar_addr;
                    // save the address
                    req_addr_d     = slave.ar_addr;
                    // save the ar_len
                    cnt_d          = 1;
                // ------------
                // Write
                // ------------
                end else if (slave.aw_valid) begin
                    slave.aw_ready = 1'b1;
                    slave.w_ready  = 1'b1;
                    addr_o         = slave.aw_addr;
                    // sample ax
                    ax_req_d       = {slave.aw_id, slave.aw_addr, slave.aw_len, slave.aw_size, slave.aw_burst};
                    // we've got our first w_valid so start the write process
                    if (slave.w_valid) begin
                        req_o          = 1'b1;
                        we_o           = 1'b1;
                        state_d        = (slave.w_last) ? SEND_B : WRITE;
                        cnt_d          = 1;
                    // we still have to wait for the first w_valid to arrive
                    end else
                        state_d = WAIT_WVALID;
                end
            end

            // ~> we are still missing a w_valid
            WAIT_WVALID: begin
                slave.w_ready = 1'b1;
                addr_o = ax_req_q.addr;
                // we can now make our first request
                if (slave.w_valid) begin
                    req_o          = 1'b1;
                    we_o           = 1'b1;
                    state_d        = (slave.w_last) ? SEND_B : WRITE;
                    cnt_d          = 1;
                end
            end

            READ: begin
                // keep request to memory high
                req_o  = 1'b1;
                addr_o = req_addr_q;
                // send the response
                slave.r_valid = 1'b1;
                slave.r_data  = data_i;
                slave.r_user  = user_i;
                slave.r_id    = ax_req_q.id;
                slave.r_last  = (cnt_q == ax_req_q.len + 1);

                // check that the master is ready, the slave must not wait on this
                if (slave.r_ready) begin
                    // ----------------------------
                    // Next address generation
                    // ----------------------------
                    // handle the correct burst type
                    case (ax_req_q.burst)
                        FIXED, INCR: addr_o = cons_addr;
                        WRAP:  begin
                            // check if the address reached warp boundary
                            if (cons_addr == upper_wrap_boundary) begin
                                addr_o = wrap_boundary;
                            // address warped beyond boundary
                            end else if (cons_addr > upper_wrap_boundary) begin
                                addr_o = ax_req_q.addr + ((cnt_q - ax_req_q.len) << LOG_NR_BYTES);
                            // we are still in the incremental regime
                            end else begin
                                addr_o = cons_addr;
                            end
                        end
                    endcase
                    // we need to change the address here for the upcoming request
                    // we sent the last byte -> go back to idle
                    if (slave.r_last) begin
                        state_d = IDLE;
                        // we already got everything
                        req_o = 1'b0;
                    end
                    // save the request address for the next cycle
                    req_addr_d = addr_o;
                    // we can decrease the counter as the master has consumed the read data
                    cnt_d = cnt_q + 1;
                    // TODO: configure correct byte-lane
                end
            end
            // ~> we already wrote the first word here
            WRITE: begin

                slave.w_ready = 1'b1;

                // consume a word here
                if (slave.w_valid) begin
                    req_o         = 1'b1;
                    we_o          = 1'b1;
                    // ----------------------------
                    // Next address generation
                    // ----------------------------
                    // handle the correct burst type
                    case (ax_req_q.burst)

                        FIXED, INCR: addr_o = cons_addr;
                        WRAP:  begin
                            // check if the address reached warp boundary
                            if (cons_addr == upper_wrap_boundary) begin
                                addr_o = wrap_boundary;
                            // address warped beyond boundary
                            end else if (cons_addr > upper_wrap_boundary) begin
                                addr_o = ax_req_q.addr + ((cnt_q - ax_req_q.len) << LOG_NR_BYTES);
                            // we are still in the incremental regime
                            end else begin
                                addr_o = cons_addr;
                            end
                        end
                    endcase
                    // save the request address for the next cycle
                    req_addr_d = addr_o;
                    // we can decrease the counter as the master has consumed the read data
                    cnt_d = cnt_q + 1;

                    if (slave.w_last)
                        state_d = SEND_B;
                end
            end
            // ~> send a write acknowledge back
            SEND_B: begin
                slave.b_valid = 1'b1;
                slave.b_id    = ax_req_q.id;
                if (slave.b_ready)
                    state_d = IDLE;
            end

        endcase
    end

    `ifndef SYNTHESIS
    `ifndef VERILATOR
    // assert that only full data lane transfers allowed
    // assert property (
    //   @(posedge clk_i) slave.aw_valid |-> (slave.aw_size == LOG_NR_BYTES)) else $fatal ("Only full data lane transfers allowed");
    //   assert property (
    //   @(posedge clk_i) slave.ar_valid |-> (slave.ar_size == LOG_NR_BYTES)) else $fatal ("Only full data lane transfers allowed");
    // assert property (
    //   @(posedge clk_i) slave.aw_valid |-> (slave.ar_addr[LOG_NR_BYTES-1:0] == '0)) else $fatal ("Unaligned accesses are not allowed at the moment");
    // assert property (
    //   @(posedge clk_i) slave.ar_valid |-> (slave.aw_addr[LOG_NR_BYTES-1:0] == '0)) else $fatal ("Unaligned accesses are not allowed at the moment");
    `endif
    `endif
    // --------------
    // Registers
    // --------------
    always_ff @(posedge clk_i or negedge rst_ni) begin
        if (~rst_ni) begin
            state_q    <= IDLE;
            ax_req_q  <= '0;
            req_addr_q <= '0;
            cnt_q      <= '0;
        end else begin
            state_q    <= state_d;
            ax_req_q   <= ax_req_d;
            req_addr_q <= req_addr_d;
            cnt_q      <= cnt_d;
        end
    end
endmodule
