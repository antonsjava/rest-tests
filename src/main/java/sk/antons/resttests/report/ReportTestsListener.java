package sk.antons.resttests.report;

import sk.antons.resttests.tests.RestTest;
import sk.antons.resttests.tests.RestTestProcessingListener;


/**
 * Listener for creating text report data.
 * @author antons
 */
public class ReportTestsListener implements RestTestProcessingListener {

    ReportedTests tests = ReportedTests.instance();

    public ReportTestsListener(ReportedTests tests) {
        this.tests = tests;
    }
    public static ReportTestsListener instance() { return new ReportTestsListener(ReportedTests.instance()); }
    public static ReportTestsListener instance(ReportedTests tests) { return new ReportTestsListener(tests); }

    public ReportedTests tests() { return tests; }

    @Override
    public void testStart(RestTest test) {
        if(test == null) return;
        ReportedTest rtest = reportedTest(test);
        tests.register(rtest);
        rtest.start();
    }

    @Override
    public void testSkip(RestTest test) {
        if(test == null) return;
        ReportedTest rtest = tests.testById(test.id());
        if(rtest == null) {
            rtest = reportedTest(test);
            tests.register(rtest);
        }
        rtest.skip(test.reason());
    }

    @Override
    public void testAbort(RestTest test) {
        if(test == null) return;
        ReportedTest rtest = tests.testById(test.id());
        if(rtest == null) {
            rtest = reportedTest(test);
            tests.register(rtest);
        }
        rtest.abort(test.reason());
    }

    @Override
    public void testFail(RestTest test) {
        if(test == null) return;
        ReportedTest rtest = tests.testById(test.id());
        if(rtest == null) {
            rtest = reportedTest(test);
            tests.register(rtest);
        }
        rtest.fail(test.reason());
    }

    @Override
    public void testDone(RestTest test) {
        if(test == null) return;
        ReportedTest rtest = tests.testById(test.id());
        if(rtest == null) {
            rtest = reportedTest(test);
            tests.register(rtest);
        }
        rtest.done();
    }


    private ReportedTest reportedTest(RestTest test) {
        return ReportedTest.prepare(test.id(), test.category(), test.name());
    }
}
