# cd /home/yosys/bachelor/simple_proc_generated/syn
cd /* PATH */
export LR_VERIF_OUT_DIR=/* Output Directory */

mkdir -p "$LR_VERIF_OUT_DIR"

# Convert core sources
for file in ../rtl/*.sv; do
  module=`basename -s .sv $file`

  sv2v \
    --define=SYNTHESIS --define=YOSYS --define=CTR_FORMAL /* Additional Definitions */ \
    $file \
    > $LR_VERIF_OUT_DIR/${module}.v
done

# Insert assertions from formal.sv into generated top.v as sv2v would remove them
sed -i '/endmodule/i MARKER' $LR_VERIF_OUT_DIR/top.v
sed -i -e '/MARKER/r formal.sv' -e '/MARKER/d' $LR_VERIF_OUT_DIR/top.v

# copy symbiyosys scripts
cp verif.sby $LR_VERIF_OUT_DIR/verif.sby
cp verif_cover.sby $LR_VERIF_OUT_DIR/verif_cover.sby
cd $LR_VERIF_OUT_DIR/

# run symbiyosys
sby -f /* SBY File */
