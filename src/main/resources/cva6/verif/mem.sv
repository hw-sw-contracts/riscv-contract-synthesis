`define NO_OP     32'h00000013
`define MAX_INSTR 128
`define COUNT 32
`define BOOT_ADDR     64'h00001000

module mem #(
    parameter int unsigned        ID   = 0
) (
    input logic clk_i,
    input logic enable_i,
    input logic req_i,
    input logic we_i,
    input logic [63:0] addr_i,
    input logic [3:0] be_i,
    input logic [63:0] data_i,
    output logic [63:0] data_o
);
    (* nomem2reg *)
    logic [31:0] instr_mem   [(`MAX_INSTR - 1):0];

    logic [31:0] last_addr   [`COUNT - 1:0];
    logic [7:0]  last_values [`COUNT - 1:0];

    logic [63:0] instr_addr;
    assign instr_addr = addr_i != 64'h0 ? addr_i - `BOOT_ADDR : addr_i;


    logic [31:0] instr_1;
    logic [31:0] instr_2;
    logic [31:0] data_1;
    logic [31:0] data_2;
    logic        has_data_1;
    logic        has_data_2;

    initial begin
        last_addr = 0;
        last_values = 0;
		for(int i = 0; i < `MAX_INSTR; i = i+1) begin
			instr_mem[i] = `NO_OP;
		end
        
//		$readmemh({"init_", ID, ".dat"}, instr_mem, 0, 31);
//		$readmemh({"memory_", ID, ".dat"}, instr_mem, 32, (`MAX_INSTR - 1));
		
        data_o <= {`NO_OP, `NO_OP};
    end

    integer i;
    //integer j;

    always @(posedge clk_i) begin

        if (req_i == 1'b1) begin
            instr_1 = (enable_i && ((instr_addr >> 2) <= (`MAX_INSTR - 1))) ? instr_mem[(instr_addr >> 2)] : `NO_OP;
            instr_2 = (enable_i && ((instr_addr >> 2) <= (`MAX_INSTR - 2))) ? instr_mem[(instr_addr >> 2) + 1] : `NO_OP;
            data_1 = 32'b0;
            data_2 = 32'b0;
            has_data_1 = 0;
            has_data_2 = 0;
                for (i = 0; i < `COUNT; i = i + 1) begin
                    if ((addr_i + 0) == last_addr[i]) begin
                        data_1[(0 * 8) + 7:(0 * 8)] = last_values[i];
                        has_data_1 = 1;
                    end
                    if ((addr_i + 4 + 0) == last_addr[i]) begin
                        data_2[(0 * 8) + 7:(0 * 8)] = last_values[i];
                        has_data_2 = 1;
                    end
                end
                for (i = 0; i < `COUNT; i = i + 1) begin
                    if ((addr_i + 1) == last_addr[i]) begin
                        data_1[(1 * 8) + 7:(1 * 8)] = last_values[i];
                        has_data_1 = 1;
                    end
                    if ((addr_i + 4 + 1) == last_addr[i]) begin
                        data_2[(1 * 8) + 7:(1 * 8)] = last_values[i];
                        has_data_2 = 1;
                    end
                end
                for (i = 0; i < `COUNT; i = i + 1) begin
                    if ((addr_i + 2) == last_addr[i]) begin
                        data_1[(2 * 8) + 7:(2 * 8)] = last_values[i];
                        has_data_1 = 1;
                    end
                    if ((addr_i + 4 + 2) == last_addr[i]) begin
                        data_2[(2 * 8) + 7:(2 * 8)] = last_values[i];
                        has_data_2 = 1;
                    end
                end
                for (i = 0; i < `COUNT; i = i + 1) begin
                    if ((addr_i + 3) == last_addr[i]) begin
                        data_1[(3 * 8) + 7:(3 * 8)] = last_values[i];
                        has_data_1 = 1;
                    end
                    if ((addr_i + 4 + 3) == last_addr[i]) begin
                        data_2[(3 * 8) + 7:(3 * 8)] = last_values[i];
                        has_data_2 = 1;
                    end
                end
            data_o <= {has_data_2 ? data_2 : instr_2, has_data_1 ? data_1 : instr_1};
            if (we_i) begin
                if (be_i[0]) begin
                    for (i = 1; i < `COUNT; i = i + 1) begin
                        last_addr[i-1] = last_addr[i];
                        last_values[i-1] = last_values[i];
                    end
                    last_addr[`COUNT] = addr_i + 0;
                    last_values[`COUNT] = data_i[(0 * 8) + 7:(0 * 8)];
                end
                if (be_i[1]) begin
                    for (i = 1; i < `COUNT; i = i + 1) begin
                        last_addr[i-1] = last_addr[i];
                        last_values[i-1] = last_values[i];
                    end
                    last_addr[`COUNT] = addr_i + 1;
                    last_values[`COUNT] = data_i[(1 * 8) + 7:(1 * 8)];
                end
                if (be_i[2]) begin
                    for (i = 1; i < `COUNT; i = i + 1) begin
                        last_addr[i-1] = last_addr[i];
                        last_values[i-1] = last_values[i];
                    end
                    last_addr[`COUNT] = addr_i + 2;
                    last_values[`COUNT] = data_i[(2 * 8) + 7:(2 * 8)];
                end
                if (be_i[3]) begin
                    for (i = 1; i < `COUNT; i = i + 1) begin
                        last_addr[i-1] = last_addr[i];
                        last_values[i-1] = last_values[i];
                    end
                    last_addr[`COUNT] = addr_i + 3;
                    last_values[`COUNT] = data_i[(3 * 8) + 7:(3 * 8)];
                end
            end
        end
    end
endmodule