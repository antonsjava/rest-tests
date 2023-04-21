/*
 *
 */
package sk.antons.resttests.report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import sk.antons.jaul.Is;
import sk.antons.jaul.util.AsRuntimeEx;

/**
 * Simple text report for executed tests.
 * @author antons
 */
public class TextReport {
    private ReportedTests tests;

    public TextReport(ReportedTests tests) {
        this.tests = tests;
    }
    public static TextReport instance(ReportedTests tests) { return new TextReport(tests); }

    public void generate(Appendable writer) {
        int allCount = 0;
        int okCount = 0;
        int failCount = 0;
        int abortCount = 0;
        int skipCount = 0;
        try {
            writer.append("--------------------------\n");
            writer.append("- junit report ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)).append('\n');
            for(String message : tests.messages()) {
                writer.append("- ").append(message).append('\n');
            }
            writer.append("--------------------------\n");
            String oldgroup = "something impossible";
            List<ReportedTest> list = tests.tests();
            for(ReportedTest test : list) {
                String group = test.group();
                if(group == null) group = "";
                if(!oldgroup.equals(group)) {
                    writer.append("test group: ").append(group).append('\n');
                }
                oldgroup = group;
                writer.append("  ")
                    .append(String.format("%-10.10s",String.valueOf(test.result())))
                    .append(" ").append(test.name())
                    .append(" in ").append("" + test.time()).append("ms");
                if(test.error() != null) writer.append(" -- ").append(test.error().toString());
                writer.append('\n');
                if(!Is.empty(test.messages())) {
                    for(String message : test.messages()) {
                        writer.append("    ").append(message).append('\n');
                    }
                }

                allCount++;
                if(test.result() == ReportedTest.Result.ABORTED) abortCount++;
                if(test.result() == ReportedTest.Result.FAILED) failCount++;
                if(test.result() == ReportedTest.Result.SKIPPED) skipCount++;
                if(test.result() == ReportedTest.Result.SUCCESSFUL) okCount++;
            }
            writer.append("--------------------------\n");
            writer.append("test counts all: ").append("" + allCount);
            writer.append("   ok: ").append("" + okCount);
            writer.append("   fail: ").append("" + failCount);
            writer.append("   abort: ").append("" + abortCount);
            writer.append("   skip: ").append("" + skipCount);
            writer.append('\n');
            writer.append("--------------------------\n");
        } catch(Exception e) {
            throw AsRuntimeEx.argument(e, "unable to generate text report");
        }

    }

}
