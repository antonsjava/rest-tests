/*
 *
 */
package sk.antons.resttests.report;

import java.util.ArrayList;
import java.util.List;

/**
 * Data of executed test
 * @author antons
 */
public class ReportedTest implements Comparable<ReportedTest> {

    private long starttime = 0;
    private long endtime = 0;

    private String id;
    private String group;
    private String name;
    private List<String> messages = new ArrayList<>();
    private Throwable error;
    private Result result = Result.UNFINISHED;

    private ReportedTest() {}

    public static ReportedTest prepare(String id, String group, String name) {
        if(id == null) throw new IllegalArgumentException("No id specified");
        if(name == null) name = "";
        if(group == null) group = "";
        ReportedTest test = new ReportedTest();
        test.id = id;
        test.group = group;
        test.name = name;
        return test;
    }

    public ReportedTest start() {
        this.starttime = System.currentTimeMillis();
        return this;
    }
    public ReportedTest done() {
        this.endtime = System.currentTimeMillis();
        this.result = Result.SUCCESSFUL;
        return this;
    }
    public ReportedTest skip(Throwable t) {
        this.endtime = System.currentTimeMillis();
        this.result = Result.SKIPPED;
        this.error = t;
        return this;
    }
    public ReportedTest fail(Throwable t) {
        this.endtime = System.currentTimeMillis();
        this.result = Result.FAILED;
        this.error = t;
        return this;
    }
    public ReportedTest abort(Throwable t) {
        this.endtime = System.currentTimeMillis();
        this.result = Result.ABORTED;
        this.error = t;
        return this;
    }
    public ReportedTest message(String message) {
        if(message != null) {
            this.messages.add(message);
        }
        return this;
    }

    public String id() { return id; }
    public String name() { return name; }
    public String group() { return group; }
    public List<String> messages() { return messages; }
    public Throwable error() { return error; }
    public Result result() { return result; }

    public long time() {
        if(starttime == 0) return 0;
        if(endtime == 0) return 0;
        return endtime - starttime;
    }
    public String fullName() {
        return group + "." + name;
    }


    public static enum Result { UNFINISHED, SKIPPED, ABORTED, FAILED, SUCCESSFUL }

    @Override
    public int compareTo(ReportedTest t) {
        String s1 = group + "." + name;
        String s2 = t == null ? "" : t.group + "." + t.name;
        return s1.compareTo(s2);
    }



}
