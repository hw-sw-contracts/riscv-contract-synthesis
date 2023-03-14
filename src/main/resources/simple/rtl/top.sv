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

    logic [15:0] instr_addr_1;
    logic [15:0] instr_addr_2;
    logic [31:0] instr_1;
    logic [31:0] instr_2;

    logic retire_1;
    logic retire_2;
    logic [31:0] retire_instr_1;
    logic [31:0] retire_instr_2;
    logic fetch_1;
    logic fetch_2;
    logic [7:0][31:0] regfile_1;
    logic [7:0][31:0] regfile_2;

    logic retire;
    logic atk_equiv;
    logic ctr_equiv;

    logic enable_1;
    logic enable_2;
    logic finished;

    instr_mem_1 instr_mem_1 (
        .clk_i                  (clock_1),
        .enable_i               (enable_1),
        .instr_addr_i           (instr_addr_1),
        .instr_o                (instr_1),
    );

    instr_mem_2 instr_mem_2 (
        .clk_i                  (clock_2),
        .enable_i               (enable_2),
        .instr_addr_i           (instr_addr_2),
        .instr_o                (instr_2),
    );

`ifdef PIPELINE
    
    core_pipeline core_1 (
        .clock_i                (clock_1),
        .instr_addr_o           (instr_addr_1),
        .instr_i                (instr_1),
        .retire_o               (retire_1),
        .retire_instr_o         (retire_instr_1),
        .fetch_o                (fetch_1),
        .regfile_o              (regfile_1),
    );

    core_pipeline core_2 (
        .clock_i                (clock_2),
        .instr_addr_o           (instr_addr_2),
        .instr_i                (instr_2),
        .retire_o               (retire_2),
        .retire_instr_o         (retire_instr_2),
        .fetch_o                (fetch_2),
        .regfile_o              (regfile_2),
    );


`else
    
    core core_1 (
        .clock_i                (clock_1),
        .instr_addr_o           (instr_addr_1),
        .instr_i                (instr_1),
        .retire_o               (retire_1),
        .retire_instr_o         (retire_instr_1),
        .fetch_o                (fetch_1),
        .regfile_o              (regfile_1),
    );

    core core_2 (
        .clock_i                (clock_2),
        .instr_addr_o           (instr_addr_2),
        .instr_i                (instr_2),
        .retire_o               (retire_2),
        .retire_instr_o         (retire_instr_2),
        .fetch_o                (fetch_2),
        .regfile_o              (regfile_2),
    );

`endif

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

endmodule
