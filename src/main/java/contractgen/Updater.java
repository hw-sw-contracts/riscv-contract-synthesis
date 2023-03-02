package contractgen;

import java.util.List;
import java.util.Set;

public interface Updater {

    Set<Observation> update(List<TestResult> testResults);
}
