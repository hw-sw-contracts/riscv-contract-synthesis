module clk_sync(
    input logic clk_i,
    input logic retire_1_i,
    input logic retire_2_i,
    output logic clk_1_o,
    output logic clk_2_o,
    output logic retire_o,
);
    initial begin
        clk_1_o <= 0;
        clk_2_o <= 0;
        //retire_o <= 0;
    end
    // Delays one clock until both are ready for retirement
    // Assumes that retire can only change when the clock is changing
    always @(clk_i) begin
        if (retire_1_i == retire_2_i) begin
            clk_1_o <= clk_i;
            clk_2_o <= clk_i;
            //retire_o <= retire_1_i;
        end
        else if (retire_1_i == 1) begin
            clk_1_o <= clk_1_o;
            clk_2_o <= clk_i;
            //retire_o <= 0;
        end
        else if(retire_2_i == 1) begin
            clk_1_o <= clk_i;
            clk_2_o <= clk_2_o;
            //retire_o <= 0;
        end
    end
    assign retire_o = retire_1_i && retire_2_i;
endmodule