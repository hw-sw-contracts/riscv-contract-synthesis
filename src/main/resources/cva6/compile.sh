cd "$1" || exit
export LR_VERIF_OUT_DIR=$2
rm -r "$LR_VERIF_OUT_DIR"
mkdir -p "$LR_VERIF_OUT_DIR"


#-------------------------------------------------------------------------
# use sv2v to convert all SystemVerilog files to Verilog
#-------------------------------------------------------------------------
export directories=( \
      "verif/*.sv" \
    );

# Print array values in  lines
for file in ${directories[*]}; do
  module=$(basename -s .sv "$file")
  if [[ "$module" == *_pkg ]]; then
    continue
  fi
  if [[ "$module" == *_intf ]]; then
    continue
  fi
  sv2v -v \
    --define=SYNTHESIS \
    --define=CONTRACT \
    --define=VERILATOR \
    --define=RVFI_TRACE \
    --define=RVFI_MEM \
	./core/core/include/ariane_dm_pkg.sv \
	./core/core/include/riscv_pkg.sv \
	./core/core/include/cv32a6_imac_sv32_config_pkg.sv \
	./core/core/include/ariane_pkg.sv \
	./core/vendor/pulp-platform/axi/src/axi_pkg.sv \
	./include/cva6_axi_pkg.sv \
	./include/rvfi_pkg.sv \
    "$file" \
    > "$LR_VERIF_OUT_DIR"/"${module}".v
done

cp cva6.v "$LR_VERIF_OUT_DIR"/cva6.v
sed -i '/assert/d' "$LR_VERIF_OUT_DIR"/cva6.v
sed -i "s/assign _0225_ = cl_tag_valid_rdata\[32'd3\];/assign cl_tag_valid_rdata[32'd3] = _0225_;/g" "$LR_VERIF_OUT_DIR"/cva6.v
sed -i "s/assign _0224_ = cl_tag_valid_rdata\[32'd2\];/assign cl_tag_valid_rdata[32'd2] = _0224_;/g" "$LR_VERIF_OUT_DIR"/cva6.v
sed -i "s/assign _0223_ = cl_tag_valid_rdata\[32'd1\];/assign cl_tag_valid_rdata[32'd1] = _0223_;/g" "$LR_VERIF_OUT_DIR"/cva6.v
sed -i "s/assign _0222_ = cl_tag_valid_rdata\[32'd0\];/assign cl_tag_valid_rdata[32'd0] = _0222_;/g" "$LR_VERIF_OUT_DIR"/cva6.v
sed -i "s/reg \[22:0\] cl_tag_valid_rdata \[3:0\];/wire [22:0] cl_tag_valid_rdata [3:0];/g" "$LR_VERIF_OUT_DIR"/cva6.v

# Insert content from formal.prop into generated top.v as sv2v would remove it
sed -i '/endmodule/i MARKER' "$LR_VERIF_OUT_DIR"/top.v
sed -i -e '/MARKER/e cat verif\/vcd.prop' -e '/MARKER/d' "$LR_VERIF_OUT_DIR"/top.v

# Read initial memory content from files
# shellcheck disable=SC2016
sed -i '/\/\/ Trace: verif\/mem.sv:45:9/i $readmemh({"init_", $sformatf("%0d", ID), ".dat"}, instr_mem, 0, 31);' "$LR_VERIF_OUT_DIR"/mem.v
# shellcheck disable=SC2016
sed -i '/\/\/ Trace: verif\/mem.sv:45:9/i $readmemh({"memory_", $sformatf("%0d", ID), ".dat"}, instr_mem, 32, (128 - 1));' "$LR_VERIF_OUT_DIR"/mem.v


# shellcheck disable=SC2016
sed -i '/\/\/ Trace: verif\/control.sv:22:9/i $readmemh({"count.dat"}, counters, 0, 0);' "$LR_VERIF_OUT_DIR"/control.v

cd "$LR_VERIF_OUT_DIR"/ || exit

# shellcheck disable=SC2035
iverilog -o ariane *.v
