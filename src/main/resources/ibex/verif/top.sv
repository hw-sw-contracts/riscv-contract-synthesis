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


    logic instr_req_1;
    logic instr_req_2;
    logic [31:0] instr_addr_1;
    logic [31:0] instr_addr_2;
    logic instr_gnt_1;
    logic instr_gnt_2;
    logic [31:0] instr_1;
    logic [31:0] instr_2;


    logic data_req_1;
    logic data_req_2;
    logic data_we_1;
    logic data_we_2;
    logic [3:0] data_be_1;
    logic [3:0] data_be_2;
    logic [31:0] data_addr_1;
    logic [31:0] data_addr_2;
    logic data_gnt_1;
    logic data_gnt_2;
    logic data_rvalid_1;
    logic data_rvalid_2;
    logic [31:0] data_rdata_1;
    logic [31:0] data_rdata_2;
    logic [31:0] data_wdata_1;
    logic [31:0] data_wdata_2;
    logic data_err_1;
    logic data_err_2;

    logic        irq_x_ack_1;
    logic        irq_x_ack_2;
    logic [4:0]  irq_x_ack_id_1;
    logic [4:0]  irq_x_ack_id_2;

    logic alert_major_1;
    logic alert_major_2;
    logic alert_minor_1;
    logic alert_minor_2;
    logic core_sleep_1;
    logic core_sleep_2;


    logic retire_1;
    logic retire_2;
    logic [31:0] retire_instr_1;
    logic [31:0] retire_instr_2;
    logic fetch_1;
    logic fetch_2;
    logic [31:0][31:0] regfile_1;
    logic [31:0][31:0] regfile_2;
    logic [31:0][31:0] mem_addr_1;
    logic [31:0][31:0] mem_addr_2;
    logic [7:0][31:0] mem_data_1;
    logic [7:0][31:0] mem_data_2;

    logic retire;
    logic atk_equiv;
    logic ctr_equiv;

    logic enable_1;
    logic enable_2;
    logic finished;

    instr_mem #(
        .ID                     (1),
    ) instr_mem_1 (
        .clk_i                  (clock_1),
        .enable_i               (enable_1),
        .instr_req_i            (instr_req_1),
        .instr_addr_i           (instr_addr_1),
        .instr_gnt_o            (instr_gnt_1),
        .instr_o                (instr_1),
    );

    instr_mem #(
        .ID                     (2),
    ) instr_mem_2 (
        .clk_i                  (clock_2),
        .enable_i               (enable_2),
        .instr_req_i            (instr_req_2),
        .instr_addr_i           (instr_addr_2),
        .instr_gnt_o            (instr_gnt_2),
        .instr_o                (instr_2),
    );

    data_mem data_mem_1 (
        .clk_i                  (clock_1),
        .data_req_i             (data_req_1),
        .data_we_i              (data_we_1),
        .data_be_i              (data_be_1),
        .data_addr_i            (data_addr_1),
        .data_wdata_i           (data_wdata_1),
        .data_gnt_o             (data_gnt_1),
        .data_rvalid_o          (data_rvalid_1),
        .data_rdata_o           (data_rdata_1),
        .data_err_o             (data_err_1),
        .mem_addr_o             (mem_addr_1),
        .mem_data_o             (mem_data_1),
    );

    data_mem data_mem_2 (
        .clk_i                  (clock_2),
        .data_req_i             (data_req_2),
        .data_we_i              (data_we_2),
        .data_be_i              (data_be_2),
        .data_addr_i            (data_addr_2),
        .data_wdata_i           (data_wdata_2),
        .data_gnt_o             (data_gnt_2),
        .data_rvalid_o          (data_rvalid_2),
        .data_rdata_o           (data_rdata_2),
        .data_err_o             (data_err_2),
        .mem_addr_o             (mem_addr_2),
        .mem_data_o             (mem_data_2),
    );

    ibex_core #(
        .RV32M                  (ibex_pkg::RV32MSingleCycle),
        .WritebackStage         (1'b1),
    ) core_1 (
        .clk_i                  (clock_1),
        .rst_ni                 (reset_1),
        .test_en_i              (1'b0),
        .hart_id_i              (32'hF11),
        .boot_addr_i            (32'h0), // TODO fix boot address (first pc probably value + 0x80)

        .instr_req_o            (instr_req_1),
        .instr_gnt_i            (instr_gnt_1),
        .instr_rvalid_i         (instr_gnt_1),
        .instr_addr_o           (instr_addr_1),
        .instr_rdata_i          (instr_1),
        .instr_err_i            (1'b0),

        .data_req_o             (data_req_1),
        .data_gnt_i             (data_gnt_1),
        .data_rvalid_i          (data_rvalid_1),
        .data_we_o              (data_we_1),
        .data_be_o              (data_be_1),
        .data_addr_o            (data_addr_1),
        .data_wdata_o           (data_wdata_1),
        .data_rdata_i           (data_rdata_1),
        .data_err_i             (data_err_1),

        .irq_software_i         (1'b0),
        .irq_timer_i            (1'b0),
        .irq_external_i         (1'b0),
        .irq_fast_i             (15'b0),
        .irq_nm_i               (1'b0),
        .irq_x_i                (32'b0),
        .irq_x_ack_o            (irq_x_ack_1),
        .irq_x_ack_id_o         (irq_x_ack_id_1),

        .external_perf_i        (16'b0),

        .debug_req_i            (1'b0),

        .fetch_enable_i         (enable_1), //TODO look at this
        .alert_major_o          (alert_major_1),
        .alert_minor_o          (alert_minor_1),
        .core_sleep_o           (core_sleep_1),

        .fetch_o                (fetch_1),
        .retire_o               (retire_1),
        .retire_instr_o         (retire_instr_1),
        .regfile_o              (regfile_1),
    );

    ibex_core #(
        .RV32M                  (ibex_pkg::RV32MSingleCycle),
        .WritebackStage         (1'b1),
    ) core_2 (
        .clk_i                  (clock_2),
        .rst_ni                 (reset_2),
        .test_en_i              (1'b0),
        .hart_id_i              (32'hF11),
        .boot_addr_i            (32'h0), // TODO fix boot address (first pc probably value + 0x80)

        .instr_req_o            (instr_req_2),
        .instr_gnt_i            (instr_gnt_2),
        .instr_rvalid_i         (instr_gnt_2),
        .instr_addr_o           (instr_addr_2),
        .instr_rdata_i          (instr_2),
        .instr_err_i            (1'b0),

        .data_req_o             (data_req_2),
        .data_gnt_i             (data_gnt_2),
        .data_rvalid_i          (data_rvalid_2),
        .data_we_o              (data_we_2),
        .data_be_o              (data_be_2),
        .data_addr_o            (data_addr_2),
        .data_wdata_o           (data_wdata_2),
        .data_rdata_i           (data_rdata_2),
        .data_err_i             (data_err_2),

        .irq_software_i         (1'b0),
        .irq_timer_i            (1'b0),
        .irq_external_i         (1'b0),
        .irq_fast_i             (15'b0),
        .irq_nm_i               (1'b0),
        .irq_x_i                (32'b0),
        .irq_x_ack_o            (irq_x_ack_2),
        .irq_x_ack_id_o         (irq_x_ack_id_2),

        .external_perf_i        (16'b0),

        .debug_req_i            (1'b0),

        .fetch_enable_i         (enable_2), //TODO look at this
        .alert_major_o          (alert_major_2),
        .alert_minor_o          (alert_minor_2),
        .core_sleep_o           (core_sleep_2),

        .fetch_o                (fetch_2),
        .retire_o               (retire_2),
        .retire_instr_o         (retire_instr_2),
        .regfile_o              (regfile_2),
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
        .regfile_1_i            (regfile_1),
        .regfile_2_i            (regfile_2),
        .mem_addr_1_i           (mem_addr_1),
        .mem_addr_2_i           (mem_addr_2),
        .mem_data_1_i           (mem_data_1),
        .mem_data_2_i           (mem_data_2),
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
        .fetch_1_i              (fetch_1),
        .fetch_2_i              (fetch_2),
        .instr_addr_1_i         (instr_addr_1),
        .instr_addr_2_i         (instr_addr_2),
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
