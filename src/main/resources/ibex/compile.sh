cd "$1" || exit
export LR_VERIF_OUT_DIR=$2
rm -r "$LR_VERIF_OUT_DIR"
mkdir -p "$LR_VERIF_OUT_DIR"


#-------------------------------------------------------------------------
# use sv2v to convert all SystemVerilog files to Verilog
#-------------------------------------------------------------------------
export directories=( \
      "core/rtl/*.sv" \
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
    ./core/rtl/*_pkg.sv \
    -I./core/vendor/lowrisc_ip/ip/prim/rtl \
    "$file" \
    > "$LR_VERIF_OUT_DIR"/"${module}".v
done

# remove generated *pkg.v files (they are empty files and not needed)
rm -f "$LR_VERIF_OUT_DIR"/*_pkg.v

# remove tracer (not needed for synthesis)
rm -f "$LR_VERIF_OUT_DIR"/ibex_tracer.v
rm -f "$LR_VERIF_OUT_DIR"/ibex_core_tracing.v

# remove the FPGA & latch-based register file (because we will use the
# flipflop-based one instead)
rm -f "$LR_VERIF_OUT_DIR"/ibex_register_file_latch.v
rm -f "$LR_VERIF_OUT_DIR"/ibex_register_file_fpga.v

# Insert content from formal.prop into generated top.v as sv2v would remove it
sed -i '/endmodule/i MARKER' "$LR_VERIF_OUT_DIR"/top.v
sed -i -e '/MARKER/e cat verif\/formal.prop' -e '/MARKER/d' "$LR_VERIF_OUT_DIR"/top.v

# Read initial memory content from files
# shellcheck disable=SC2016
sed -i '/\/\/ Trace: verif\/instr_mem.sv:31:9/i $readmemh({"init_", $sformatf("%0d", ID), ".dat"}, mem, 0, 31);' "$LR_VERIF_OUT_DIR"/instr_mem.v
# shellcheck disable=SC2016
sed -i '/\/\/ Trace: verif\/instr_mem.sv:31:9/i $readmemh({"memory_", $sformatf("%0d", ID), ".dat"}, mem, 32, (128 - 1));' "$LR_VERIF_OUT_DIR"/instr_mem.v

# shellcheck disable=SC2016
sed -i '/\/\/ Trace: verif\/control.sv:22:9/i $readmemh({"count.dat"}, counters, 0, 0);' "$LR_VERIF_OUT_DIR"/control.v

cd "$LR_VERIF_OUT_DIR"/ || exit

# shellcheck disable=SC2035
iverilog -o ibex *.v