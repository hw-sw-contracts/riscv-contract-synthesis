module clk_sync(
    input logic clk_i,
    input logic valid_1_1_i,
    input logic valid_1_2_i,
    input logic valid_2_1_i,
    input logic valid_2_2_i,
    input logic [31:0] pc_rdata_1_1_i,
    input logic [31:0] pc_rdata_1_2_i,
    input logic [31:0] pc_rdata_2_1_i,
    input logic [31:0] pc_rdata_2_2_i,
    output logic clk_1_o,
    output logic clk_2_o,
    output logic sel_1_o,
    output logic sel_2_o,
    output logic retire_o,
);
    logic both = 0;
    integer go = 0;
    logic pause_1 = 0;
    logic pause_2 = 0;
    initial begin
        clk_1_o <= 0;
        clk_2_o <= 0;
        retire_o <= 0;
        sel_1_o <= 0;
        sel_2_o <= 0;
    end
    // Delays one clock until both are ready for retirement
    // Assumes that retire can only change when the clock is changing
    always @(posedge clk_i) begin
        if (pause_1) begin
            clk_1_o <= clk_1_o;
        end else begin
            clk_1_o <= clk_i;
        end
        if (pause_2) begin
            clk_2_o <= clk_2_o;
        end else begin
            clk_2_o <= clk_i;
        end
    end
    always @(negedge clk_i) begin
        if (valid_1_1_i && !valid_1_2_i || !valid_1_1_i && valid_1_2_i) begin
            //one port of 1 is ready
            if (valid_2_1_i && !valid_2_2_i || !valid_2_1_i && valid_2_2_i) begin
                //one port of both is ready, continue and retire
                pause_1 = 0;
                pause_2 = 0;
                retire_o <= 1'b1;
                sel_1_o <= valid_1_1_i ? 0 : 1;
                sel_2_o <= valid_2_1_i ? 0 : 1;
            end
            if (!valid_2_1_i && !valid_2_2_i) begin
                // 2 is not yet ready, pause 1
                pause_1 = 1;
                pause_2 = 0;
                retire_o <= 0;
            end
            if (valid_2_1_i && valid_2_2_i) begin
                if ((valid_1_1_i && (pc_rdata_1_1_i == pc_rdata_2_1_i)) || (valid_1_2_i && (pc_rdata_1_2_i == pc_rdata_2_1_i))) begin
                    sel_1_o <= valid_1_1_i ? 0 : 1;
                    sel_2_o <= 0;
                    pause_1 = 0;
                    pause_2 = 0;
                    retire_o <= 1'b1;
                end else begin
                    if ((valid_1_1_i && (pc_rdata_1_1_i == pc_rdata_2_2_i)) || (valid_1_2_i && (pc_rdata_1_2_i == pc_rdata_2_2_i))) begin
                        sel_1_o <= valid_1_1_i ? 0 : 1;
                        sel_2_o <= 1;
                        pause_1 = 0;
                        pause_2 = 0;
                        retire_o <= 1'b1;
                    end else begin
                        // 2 has both ports ready, pause 2
                        pause_1 = 0;
                        pause_2 = 1;
                        retire_o <= 0;
                    end
                end
            end
        end
        if (!valid_1_1_i && !valid_1_2_i) begin
            // 1 is not yet ready
            if (valid_2_1_i || valid_2_2_i) begin
                // 2 at least one port ready, pause 2
                pause_1 = 0;
                pause_2 = 1;
                retire_o <= 0;
            end else begin
                // none is ready, continue both
                pause_1 = 0;
                pause_2 = 0;
                retire_o <= 0;
            end
        end
        if (valid_1_1_i && valid_1_2_i) begin
            // 1 has both ports ready
            if (valid_2_1_i && valid_2_2_i) begin
                begin
                    if (go == 0) begin
                        pause_1 = 1;
                        pause_2 = 1;
                        retire_o <= 1;
                        sel_1_o <= 0;
                        sel_2_o <= 0;
                        go = 3;
                    end
                    if (go == 2) begin
                        retire_o <= 1;
                        sel_1_o <= 1;
                        sel_2_o <= 1;
                    end
                end
            end else begin
                if ((valid_2_1_i && (pc_rdata_2_1_i == pc_rdata_1_1_i)) || (valid_2_2_i && (pc_rdata_2_2_i == pc_rdata_1_1_i))) begin
                    sel_1_o <= 0;
                    sel_2_o <= valid_2_1_i ? 0 : 1;
                    pause_1 = 0;
                    pause_2 = 0;
                    retire_o <= 1'b1;
                end else begin
                    if ((valid_2_1_i && (pc_rdata_2_1_i == pc_rdata_1_2_i)) || (valid_2_2_i && (pc_rdata_2_2_i == pc_rdata_1_2_i))) begin
                        sel_1_o <= 1;
                        sel_2_o <= valid_2_1_i ? 0 : 1;
                        pause_1 = 0;
                        pause_2 = 0;
                        retire_o <= 1'b1;
                    end else begin
                        // 2 has both ports ready, pause 2
                        pause_1 = 1;
                        pause_2 = 0;
                        retire_o <= 0;
                    end
                end
            end
        end
        if (go == 1) begin
            pause_1 = 0;
            pause_2 = 0;
            retire_o <= 0;
            if (!valid_1_1_i && !valid_1_2_i || !valid_2_1_i && !valid_2_2_i) begin
                go = 0;
            end
        end
        if (go > 1) begin
            go = go - 1;
        end
        if (pause_1) begin
            clk_1_o <= clk_1_o;
        end else begin
            clk_1_o <= clk_i;
        end
        if (pause_2) begin
            clk_2_o <= clk_2_o;
        end else begin
            clk_2_o <= clk_i;
        end
    end
endmodule : clk_sync