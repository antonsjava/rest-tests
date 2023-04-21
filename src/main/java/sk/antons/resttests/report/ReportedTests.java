/*
 *
 */
package sk.antons.resttests.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Container for tests executed tests.
 * @author antons
 */
public class ReportedTests {
    private static Logger log = LoggerFactory.getLogger(ReportedTests.class);

    public static ReportedTests instance() { return new ReportedTests(); }

    private Map<String, ReportedTest> tests = new HashMap<>();
    private List<String> messages = new ArrayList<>();

    public List<String> messages() { return messages; }
    public ReportedTests message(String message) {
        if(message != null) {
            this.messages.add(message);
        }
        return this;
    }

    public synchronized void register(ReportedTest test) {
        if(test == null) {
            return ;
        }
        if(tests.containsKey(test.id())) {
            log.warn("test {} - {} already registered", test.id(), test.fullName());
            return;
        }
        tests.put(test.id(), test);
    }

    public synchronized ReportedTest testById(String id) {
        if(id == null) return null;
        return tests.get(id);
    }

    public synchronized List<ReportedTest> tests() {
        ArrayList<ReportedTest> list = new ArrayList<>(tests.values());
        Collections.sort(list);
        return list;
    }




}
