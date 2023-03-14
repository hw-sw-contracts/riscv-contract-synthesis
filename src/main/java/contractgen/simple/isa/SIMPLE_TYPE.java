package contractgen.simple.isa;

/**
 * The type of an simple instruction.
 */
@SuppressWarnings("MissingJavadoc")
public enum SIMPLE_TYPE {
    ADD("add", "00000001"),
    ADDI("addi", "00000010"),
    MUL("mul", "00000011"),
    MULI("muli","00000100"),
    NO_OP("no_op","00000000");

    private final String name;
    private final String opcode;

    SIMPLE_TYPE(String name, String opcode) {
        this.name = name;
        this.opcode = opcode;
    }

    public String getOpcode() {
        return opcode;
    }

    public String getName() {
        return name;
    }

    public String generateContract(String suffix) {
        return "if (op_" + suffix + " == " + opcode + ") begin\n";
    }
}
