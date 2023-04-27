/*
 *
 */
package sk.antons.resttests;

import java.io.File;
import java.io.FileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.antons.jaul.Get;
import sk.antons.jaul.Is;
import sk.antons.resttests.report.ReportTestsListener;
import sk.antons.resttests.report.TextReport;
import sk.antons.resttests.tests.SummaryTestsListener;
import sk.antons.util.logging.conf.SLConf;
/**
 * Simple tests runner
 * @author antons
 */
public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);

    private App app = App.instance();
    private String reportfile = null;
    private boolean inputError = false;

    public Main(String[] args) {
        inputError = !parseParams(args);
    }
    public static Main instance(String[] args) { return new Main(args); }


    private boolean parseParams(String[] args) {
        String lgname = null;
        String reportname = null;
        String match = null;
        boolean debug = false;
        for(int i = 0; i < args.length; i++) {
            String param = args[i];
            if(isProperty(param, "-p", "--param")) {
                String value = Get.nth(args, i+1);
                if(Is.empty(value)) {
                    log.error("Parameter {} value not specified. (at position {})", param, i);
                    return false;
                }
                int pos = value.indexOf('=');
                if(pos < 0) {
                    log.error("Parameter {} value has bad format. (at position {})", param, i);
                    log.error("value:  {}", value);
                    return false;
                }
                String n = value.substring(0, pos);
                String v = value.substring(pos+1);
                log.info("param identified {}=()", n, v);
                app.property(n, v);
                i++;
            } else if(isProperty(param, "-s", "--source")) {
                String value = Get.nth(args, i+1);
                if(Is.empty(value)) {
                    log.error("Parameter {} value not specified. (at position {})", param, i);
                    return false;
                }
                log.info("source identified {}", value);
                app.resource(value);
                i++;
            } else if(isProperty(param, "-se", "--sourceEnc")) {
                String value = Get.nth(args, i+1);
                if(Is.empty(value)) {
                    log.error("Parameter {} value not specified. (at position {})", param, i);
                    return false;
                }
                log.info("source encoding identified {}", value);
                app.sourceEncoding(value);
                i++;
            } else if(isProperty(param, "-ue", "--urlEnc")) {
                String value = Get.nth(args, i+1);
                if(Is.empty(value)) {
                    log.error("Parameter {} value not specified. (at position {})", param, i);
                    return false;
                }
                log.info("url encoding identified {}", value);
                app.urlEncoding(value);
                i++;
            } else if(isProperty(param, "-l", "--log")) {
                String value = Get.nth(args, i+1);
                if(Is.empty(value)) {
                    log.error("Parameter {} value not specified. (at position {})", param, i);
                    return false;
                }
                File f = new File(value);
                if(f.exists() && f.isDirectory()) {
                    log.error("Parameter {} has bad value - not a file. (at position {})", param, i);
                    log.error("value: {}", value);
                    return false;
                }
                if(!f.exists()) {
                    File p = f.getParentFile();
                    if(!p.exists()) p.mkdirs();
                }
                if(lgname == null) {
                    log.info("log file identified {}", value);
                    lgname = value;
                    i++;
                } else {
                    log.error("Only one directory/file can be specified. (at position {})", i);
                    log.error("first: {}", lgname);
                    log.error("second: {}", param);
                    return false;
                }
            } else if(isProperty(param, "-r", "--report")) {
                String value = Get.nth(args, i+1);
                if(Is.empty(value)) {
                    log.error("Parameter {} value not specified. (at position {})", param, i);
                    return false;
                }
                File f = new File(value);
                if(f.exists() && f.isDirectory()) {
                    log.error("Parameter {} has bad value - not a file. (at position {})", param, i);
                    log.error("value: {}", value);
                    return false;
                }
                if(!f.exists()) {
                    File p = f.getParentFile();
                    if(!p.exists()) p.mkdirs();
                }
                if(reportname == null) {
                    log.info("report file identified {}", value);
                    reportname = value;
                    i++;
                } else {
                    log.error("Only one file can be specified. (at position {})", i);
                    log.error("first: {}", reportname);
                    log.error("second: {}", value);
                    return false;
                }
            } else if(isProperty(param, "-inc", "--include")) {
                String value = Get.nth(args, i+1);
                if(Is.empty(value)) {
                    log.error("Parameter {} value not specified. (at position {})", param, i);
                    return false;
                }
                if(match == null) {
                    log.info("file include identified {}", value);
                    app.include(value);
                    i++;
                }
            } else if(isProperty(param, "-exc", "--exclude")) {
                String value = Get.nth(args, i+1);
                if(Is.empty(value)) {
                    log.error("Parameter {} value not specified. (at position {})", param, i);
                    return false;
                }
                if(match == null) {
                    log.info("file exclude identified {}", value);
                    app.exclude(value);
                    i++;
                }
            } else if(isProperty(param, "-d", "--debug")) {
                log.info("debug option identified");
                debug = true;
            } else if(isProperty(param, "-h", "--help")) {
                return false;
            } else if(isProperty(param, "-f", "--file", "--directory")) {
                String value = Get.nth(args, i+1);
                if(Is.empty(value)) {
                    log.error("Parameter {} value not specified. (at position {})", param, i);
                    return false;
                }
                if(match == null) {
                    log.info("file root identified {}", value);
                    app.from(value);
                    i++;
                }
            } else {
                log.error("Unknown parameter identified '{}'. (at position {})", param, i);
                return false;
            }
        }

        if(lgname == null) {
            SLConf.reset();
            SLConf.rootLogger()
            .console()
            .filterAll()
            .pattern("${time} ${level:3:-3}: ${message}")
            .handler();
            SLConf.rootLogger().info();
            if(debug) SLConf.logger("sk.antons").fine();
        } else {
            SLConf.reset();
            SLConf.rootLogger()
                .file(lgname)
                .filterAll()
                .pattern("${date} ${time} ${level:3:-3} ${sname:-20:-20}: ${message}")
                .handler();
            SLConf.rootLogger()
            .console()
            .filterInfo()
            .pattern("${time} ${level:3:-3}: ${message}")
            .handler();
            SLConf.rootLogger().info();
            if(debug) SLConf.logger("sk.antons").fine();
        }

        reportfile = reportname;

        return true;
    }

    private static boolean isProperty(String value, String... names) {
        if(Is.empty(value)) return false;
        if(!Is.empty(names)) {
            for(String name : names) {
                if(value.equals(name)) return true;
            }
        }
        return false;
    }

    private static void printUsage() {
        String info = "java -jar rest-tests.jar [-l log-file-name] [-d] \n" +
"          [-p name=value]* [-s resource-url]* \n" +
"          [-r report-file-name]  \n" +
"          test-root-directory\n" +
"   [-l|--log log-file-name] - prints log to file instead of console \n" +
"   [-d|--debug] - prints debug log output \n" +
"   [-p|--param name=value]* - define properties for placeholder replacements\n" +
"   [-s|--source resource-url]* - define source urls\n" +
"   [-r|--report report-file-name] - define output for text report (normaly it is printed only to console) \n" +
"   [-inc|--include file-path-pattern] - file mathcer used for directory scan (like **/*-test.json) \n" +
"   [-exc|--exclude file-path-pattern] - file mathcer used for directory scan (like **/*-test.json) \n" +
"   [-se|--sourceEnc encoding] - source file encoding \n" +
"   [-ue|--urlEnc encoding] - url file encoding \n" +
"   [-f|--file|--directory file/direcotry] - root for test search\n";
        log.info("Use form: {}", info);
    }

    public void start() {
        if(inputError) {
            printUsage();
            System.exit(1);
        }

        SummaryTestsListener summary = SummaryTestsListener.instance();
        ReportTestsListener report = ReportTestsListener.instance();

        app.listener(summary);
        app.listener(report);

        // start tests
        log.info("tests started");
        app.start();
        log.info("tests done");

        // print reports from report containers
        StringBuilder sb = new StringBuilder();
        summary.report(sb);
        log.info("summary: \n{}", sb);
        if(reportfile == null) {
            sb.setLength(0);
            TextReport.instance(report.tests()).generate(sb);
            log.info(sb.toString());
        } else {
            try {
                FileWriter fw = new FileWriter(reportfile);
                TextReport.instance(report.tests()).generate(fw);
                fw.flush();
                fw.close();
            } catch(Exception e) {
                log.error("unable to generate report {} {}", reportfile, e);
            }
        }

        if(summary.isAnError()) {
            log.info("result: tests failed");
            System.exit(2);
        } else {
            log.info("result: tests passed");
        }
    }

    public static void main(String[] args) {

        SLConf.reset();
        SLConf.rootLogger()
            .console()
            .pattern("${time} ${level:3:-3}: ${message}")
            .handler();
        SLConf.rootLogger().info();

        Main main = new Main(args);
        main.start();
    }

}
