package sk.antons.resttests.tests;

import sk.antons.jaul.util.AsRuntimeEx;


public class SummaryTestsListener implements RestTestProcessingListener {

    private int allCount = 0;
    private int doneCount = 0;
    private int skipCount = 0;
    private int abortCount = 0;
    private int failCount = 0;


    public static SummaryTestsListener instance() { return new SummaryTestsListener(); }

    public int allCount() { return allCount; }
    public int doneCount() { return doneCount; }
    public int skipCount() { return skipCount; }
    public int abortCount() { return abortCount; }
    public int failCount() { return failCount; }

    public boolean isAnError() { return (abortCount > 0) || (failCount > 0); }



    public void report(Appendable writer) {
        try {
            writer.append("------------------------------------------\n");
            if(allCount > (doneCount + skipCount)) writer.append("- some of tests was aborted or failed\n");
            else writer.append("- all running tests passed\n");
            writer.append("- all count:   ").append(String.valueOf(allCount)).append("\n");
            writer.append("- ok count:    ").append(String.valueOf(doneCount)).append("\n");
            writer.append("- skip count:  ").append(String.valueOf(skipCount)).append("\n");
            writer.append("- abort count: ").append(String.valueOf(abortCount)).append("\n");
            writer.append("- fail count:  ").append(String.valueOf(failCount)).append("\n");
            writer.append("------------------------------------------\n");
        } catch(Exception e) {
            throw AsRuntimeEx.argument(e);
        }
    }


    @Override
    public void testStart(RestTest test) {
        if(test == null) return;
        allCount++;
    }

    @Override
    public void testSkip(RestTest test) {
        if(test == null) return;
        skipCount++;
    }

    @Override
    public void testAbort(RestTest test) {
        if(test == null) return;
        abortCount++;
    }

    @Override
    public void testFail(RestTest test) {
        if(test == null) return;
        failCount++;
    }

    @Override
    public void testDone(RestTest test) {
        if(test == null) return;
        doneCount++;
    }


}
