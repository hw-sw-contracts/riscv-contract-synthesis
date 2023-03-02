module tc_clk_gating (
    input logic clk_i,
    input logic en_i,
	input logic test_en_i,
	input logic clk_o
);

    assign clk_o = clk_i;

endmodule