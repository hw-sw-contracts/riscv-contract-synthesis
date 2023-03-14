    always @(posedge clk) 
        assert (instr_addr_1 <= /* Max Instruction Address 1 */ || instr_addr_2 <= /* Max Instruction Address 2 */ || (!(ctr_equiv && !atk_equiv)));

    always @(posedge clk) 
        cover (finished == 1);
