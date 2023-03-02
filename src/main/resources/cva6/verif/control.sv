module control (
    input logic clk_i,
    input logic retire_i,
    input logic fetch_1_i,
    input logic fetch_2_i,
    output logic enable_1_o,
    output logic enable_2_o,
    output logic finished_o,
);
    (* nomem2reg *)
    reg [31:0] counters [0:0];
    int retire_count = 0;
    int fetch_1_count = 0;
    int fetch_2_count = 0;
    int MAX_INSTR_COUNT;

    assign MAX_INSTR_COUNT = counters[0];

    initial begin
        enable_1_o <= 1;
        enable_2_o <= 1;
        finished_o <= 0;

//        $readmemh({"count.dat"}, MAX_INSTR_COUNT, 0, 0);
    end

    always @(negedge clk_i) begin
        if (retire_i == 1)
            retire_count = retire_count + 1;
        
        if (enable_1_o == 1 && fetch_1_i == 1)
            fetch_1_count = fetch_1_count + 1;
        if (!enable_1_o || (fetch_1_count >= MAX_INSTR_COUNT && fetch_1_i))
            enable_1_o = 0; 
        
        if (enable_2_o == 1 && fetch_2_i == 1)
            fetch_2_count = fetch_2_count + 1;
        if (!enable_2_o || (fetch_2_count >= MAX_INSTR_COUNT && fetch_2_i))
            enable_2_o = 0;

        if (!enable_1_o && !enable_2_o && retire_count == MAX_INSTR_COUNT)
            finished_o <= 1;
        
    end

endmodule