/*
 *
 */
package example;

import java.io.FileReader;
import sk.antons.resttests.tests.RestTest;
import sk.antons.resttests.tests.RestTestDeserializer;
import sk.antons.resttests.tests.RestTests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.antons.resttests.http.call.SimpleHttpCall;
import sk.antons.resttests.report.ReportTestsListener;
import sk.antons.resttests.report.TextReport;
import sk.antons.resttests.tests.SummaryTestsListener;
import sk.antons.util.logging.conf.SLConf;

/**
 * Adhoc test runner. Tests are selected by code.
 * @author antons
 */
public class SimpleTestRunner {
    private static Logger log = LoggerFactory.getLogger(SimpleTestRunner.class);

    public static void main(String[] argv) {
        // just configure logging
        SLConf.simpleConsole("sk.antons");
        try {

            log.info("tests preparing");
            // prepare adhoc test for simple non templated test
            RestTest test1 = RestTestDeserializer.test(new FileReader("./src/test/example/simple/simple-get-test.json"));
            RestTest test2 = RestTestDeserializer.test(new FileReader("./src/test/example/simple/simple-get-fail-test.json"));
            RestTest test3 = RestTestDeserializer.test(new FileReader("./src/test/example/simple/simple-get-abort-test.json"));
            RestTest test4 = RestTestDeserializer.test(new FileReader("./src/test/example/simple/simple-get-skip-test.json"));

            // build test suite from prepared tests
            RestTests tests = RestTests.builder()
                .from(test1, test2, test3, test4)
                .build();


            // prepare report data containers
            SummaryTestsListener summary = SummaryTestsListener.instance(); // an summary counts for tests
            ReportTestsListener report = ReportTestsListener.instance(); // simple test results collector

            //start tests
            log.info("tests started");
            tests.processTests(
                request -> SimpleHttpCall.instance().call(request) // http caller
                , summary, report); // add report data containers
            log.info("tests done");

            // print reports from report containers
            StringBuilder sb = new StringBuilder();
            summary.report(sb); // print simple summary info
            log.info("summary: \n{}", sb);
            sb.setLength(0);
            TextReport.instance(report.tests()).generate(sb); // generate text report
            log.info(sb.toString());

            if(summary.isAnError()) log.info("result: tests failed");
            else log.info("result: tests passed");
        } catch(Exception e) {
            log.error("unable to run tests", e);
        }
    }

}
