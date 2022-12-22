package contractgen;

public interface Program {

    String printSymbolic();

    String printProgram(Integer address);

    int maxAddress(int base);

    void printInit(String path);

    void printInstr(String path);
}
