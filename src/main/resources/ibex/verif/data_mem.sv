`define COUNT 32
module data_mem (
    input  logic clk_i,
    input  logic data_req_i,
    input  logic data_we_i,
    input  logic  [3:0]  data_be_i,
    input  logic [31:0]  data_addr_i,
    input  logic [31:0]  data_wdata_i,
    output logic data_gnt_o,
    output logic data_rvalid_o,
    output logic [31:0]  data_rdata_o,
    output logic data_err_o,
    output logic [31:0] mem_addr_o [`COUNT - 1:0],
    output logic [7:0] mem_data_o [`COUNT - 1:0],
);

    logic [31:0] last_addr [`COUNT - 1:0];
    logic [7:0] last_values [`COUNT - 1:0];
    logic [32:0] temp;
    
    initial begin
        last_addr = 0;
        last_values = 0;
    end

    integer i;
    integer j;
    always @(posedge clk_i) begin
        if (data_req_i == 1'b1) begin
            temp = data_addr_i;
            for (i = 0; i < `COUNT; i = i + 1) begin
                if (data_be_i[0] && (data_addr_i + 0) == last_addr[i]) begin
                    temp[(0 * 8) + 7:(0 * 8)] = last_values[i];
                end
                if (data_be_i[1] && (data_addr_i + 1) == last_addr[i]) begin
                    temp[(1 * 8) + 7:(1 * 8)] = last_values[i];
                end
                if (data_be_i[2] && (data_addr_i + 2) == last_addr[i]) begin
                    temp[(2 * 8) + 7:(2 * 8)] = last_values[i];
                end
                if (data_be_i[3] && (data_addr_i + 3) == last_addr[i]) begin
                    temp[(3 * 8) + 7:(3 * 8)] = last_values[i];
                end
            end
            data_rdata_o <= temp;
            if (data_we_i) begin
                if (data_be_i[0]) begin
                    for (i = 1; i < `COUNT; i = i + 1) begin
                        last_addr[i-1] = last_addr[i];
                        last_values[i-1] = last_values[i];
                    end
                    last_addr[`COUNT] = data_addr_i + 0;
                    last_values[`COUNT] = data_wdata_i[(0 * 8) + 7:(0 * 8)];
                end
                if (data_be_i[1]) begin
                    for (i = 1; i < `COUNT; i = i + 1) begin
                        last_addr[i-1] = last_addr[i];
                        last_values[i-1] = last_values[i];
                    end
                    last_addr[`COUNT] = data_addr_i + 1;
                    last_values[`COUNT] = data_wdata_i[(1 * 8) + 7:(1 * 8)];
                end
                if (data_be_i[2]) begin
                    for (i = 1; i < `COUNT; i = i + 1) begin
                        last_addr[i-1] = last_addr[i];
                        last_values[i-1] = last_values[i];
                    end
                    last_addr[`COUNT] = data_addr_i + 2;
                    last_values[`COUNT] = data_wdata_i[(2 * 8) + 7:(2 * 8)];
                end
                if (data_be_i[3]) begin
                    for (i = 1; i < `COUNT; i = i + 1) begin
                        last_addr[i-1] = last_addr[i];
                        last_values[i-1] = last_values[i];
                    end
                    last_addr[`COUNT] = data_addr_i + 3;
                    last_values[`COUNT] = data_wdata_i[(3 * 8) + 7:(3 * 8)];
                end
            end
            data_gnt_o <= 1'b1;
            data_rvalid_o <= 1'b1;
            data_err_o <= 1'b0;
        end else begin
            data_gnt_o <= 1'b0;
            data_rvalid_o <= 1'b0;
            data_err_o <= 1'b0;
        end
    end

    /*
    (* nomem2reg *)
    logic [7:0] mem [31:0];

    initial begin
        mem = 0;
    end

    assign mem_o = mem;

    always @(posedge clk_i) begin
        if(data_req_i == 1'b1) begin
            // TODO figure out which bits of BE are which byte
            //TODO figure out byte order
            data_rdata_o <= {data_be_i[0] ? mem[data_addr_i] : 8'b0, data_be_i[1] ? mem[data_addr_i + 1] : 8'b0, data_be_i[2] ? mem[data_addr_i + 2] : 8'b0, data_be_i[3] ? mem[data_addr_i + 3] : 8'b0};
            if (data_we_i) begin
                if (data_be_i[0])
                    mem[data_addr_i] <= data_wdata_i[7:0];
                if (data_be_i[1])
                    mem[data_addr_i] <= data_wdata_i[15:8];
                if (data_be_i[2])
                    mem[data_addr_i] <= data_wdata_i[23:16];
                if (data_be_i[3])
                    mem[data_addr_i] <= data_wdata_i[31:24];
            end
            data_gnt_o <= 1'b1;
            data_rvalid_o <= 1'b1;
            data_err_o <= 1'b0;
        end
        else begin
            data_gnt_o <= 1'b0;
            data_rvalid_o <= 1'b0;
            data_err_o <= 1'b0;
        end
    end
    */
endmodule