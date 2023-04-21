/*
 *
 */
package example;

import java.util.Properties;
import sk.antons.resttests.tests.RestTests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.antons.jaul.util.TransitiveProperties;
import sk.antons.resttests.http.call.SimpleHttpCall;
import sk.antons.resttests.report.ReportTestsListener;
import sk.antons.resttests.report.TextReport;
import sk.antons.resttests.template.Resources;
import sk.antons.resttests.tests.SummaryTestsListener;
import sk.antons.util.logging.conf.SLConf;

/**
 * Template based tests started from root directory.
 * @author antons
 */
public class TemplateDirectoryRunner {
    private static Logger log = LoggerFactory.getLogger(TemplateDirectoryRunner.class);

    public static void main(String[] argv) {
        SLConf.simpleConsole("sk.antons"); // just logging setting
        try {

            // make some global placeholders for templates
            Properties props = new Properties();
            props.put("zuplo.url", "https://echo.zuplo.io");
            TransitiveProperties.makeClosure(props); // make closure for placeholders

            // nake some global resource sources
            Resources resources = Resources.builder()
                .addSource("./src/test/example")
                .build();

            log.info("tests preparing");
            // prepare tests from directory use name mather to identify which files are tests
            RestTests tests = RestTests.builder()
                .encoding("utf-8")
                .resources(resources) // add resources so template can reference
                .properties(props) // add placeholders for template completion
                .include("**/*-test.tmpl")
                .from("./src/test/example/template")
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
            summary.report(sb);
            log.info("summary: \n{}", sb);
            sb.setLength(0);
            TextReport.instance(report.tests()).generate(sb);
            log.info(sb.toString());

            if(summary.isAnError()) log.info("result: tests failed");
            else log.info("result: tests passed");
        } catch(Exception e) {
            log.error("unable to run tests", e);
        }
    }

}
