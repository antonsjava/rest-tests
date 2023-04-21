/*
 *
 */
package example;

import sk.antons.resttests.tests.RestTests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.antons.resttests.http.call.SimpleHttpCall;
import sk.antons.resttests.report.ReportTestsListener;
import sk.antons.resttests.report.TextReport;
import sk.antons.resttests.tests.SummaryTestsListener;
import sk.antons.util.logging.conf.SLConf;

/**
 * Starts all tests from root directory down.
 * @author antons
 */
public class SimpleDirectoryRunner {
    private static Logger log = LoggerFactory.getLogger(SimpleDirectoryRunner.class);

    public static void main(String[] argv) {
        SLConf.simpleConsole("sk.antons");
        try {

            log.info("tests preparing");
            // prepare tests from directory use name mather to identify which files are tests
            RestTests tests = RestTests.builder()
                .include("**/*-test.json")
                .from("./src/test/example/simple")
                .build();

            // prepare report data containers
            SummaryTestsListener summary = SummaryTestsListener.instance(); // an summary counts for tests
            ReportTestsListener report = ReportTestsListener.instance(); // simple test results collector

            // start tests
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
