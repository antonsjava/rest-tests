# rest-tests

 Simple framework for text based rest tests. You can write simple json based text 
 file, which define http request and some condition to check afterwards. So it can 
 be used as unit test for your API. 

 This framework allows you to write custom test applications for your tests. Some coding 
 is usually necessary, as rest API requires some additional features as custom 
 authentication, custom logging, custom test result reporting, .... 
 If you don't need such things, you can build such application in few lines. You can 
 use example applications as inspiration.

 Framework expects that tested API is text based. That means that requests and 
 responses sends some text based data. (usually json texts)  

## tests

 For test you must define data for http request and condition to be tested afterwards. 
 
 This can be as content of your test file

~~~json
{
  "category" : "simple",
  "name" : "simple-post-json",
  "skip" : false,
  "request" : {
    "url" : "https://echo.zuplo.io",
    "method" : "POST",
    "header" : {
      "Content-Type" : "application/json"
    },
    "payload": {
      "name": "John",
      "surname": "Smith"
    }
  },
  "condition" : {
    "status" : {"eq": "200"},
    "body.body.name": {"eq": "John"}
  }
}
~~~

 Main parts are:

 - category - test category name (default is directory name)
 - name - test name (default is file name)
 - skip - true if test must be skipped (default is false)
 - request - data to be send as http request [details](./request.md)
 - condition - final condition for response data [details](./condition.md)


## simple placeholders

 You can write test with simple text placeholders and in your application 
 provide properties for those placeholders. In this way you can have one 
 test file for more environments.

~~~json
{
  "request" : {
    "url" : "${my-service.url}/path/to/api",
     ...
  }
}
~~~
 
 There are following predefined placeholders

 - ${unique.global} - each occurrence is replaced by own unique number
 - ${unique.request} - all occurrences are replaced by one unique number

 All other placeholders must be provided by application. 


## template placeholders

 Test files can have another types of placeholders @{resource name}. Those 
 placeholder are replaced by another file content. File is located using 
 resource name in placeholder.

 There are following resource names

 - resource name starts with '/' is considered absolute file name like @{/home/user/tests/big-request.json}
 - resource name starts with 'classpath:' is consiered as classpath resource like @{classpath:examples/big-request.json}
 - otherwise is resource name considered as relative to one of resource sources defined by application

 You can provide resource sources in your application as 

~~~java
 Resources resources = Resources.builder()
     .addSource("/home/user/tests")
     .addSource("./src/test/example")
     .addSource("classpath:tests")
     .build();
~~~

 You can also add a line number after resource name @{/home/user/tests/big-request.json|2}. In such case 
 resource is taken as set of lines and only one specified line is returned. (lines are counted form 0 and you 
 can specify 'rand' as line number and it choose random line from resource)

 So your test can look like 

~~~json
{
  "category" : "simple",
  "name" : "simple-post-json",
  "skip" : false,
  "request" : {
    "url" : "${my-service.url}/path/to/api",
    "method" : "POST",
    "header" : {
      "Content-Type" : "application/json"
    },
    "payload": {
      "id": "id-${unique.global}",
      "name": "@{../lists/givennames.txt|rand}",
      "surname": "@{../lists/surnames.txt|rand}",
      "address": @{../lists/address.txt|rand}
    }
  },
  "condition" : {
    "status" : {"eq": "200"}
  }
}
~~~


## applications

 Finally you must create your application, which start tests. You must somehow 
 define following 

 - define placeholder values (as Properties instance).
 - define resource sources (as Resources instance).
 - define set of tests by specifying root directory and format of path/filename. (if pattern contains '/' full path must match otherwise only filename must match. '\*\*' means anything, '\*' means anything except '/')  

~~~java
   RestTests tests = RestTests.builder()
       .encoding("utf-8")
       .resources(resources)
       .properties(props)
       .fromDirectory("./src/test/example/template", "**/*-test.tmpl");
~~~

 - implement an http caller (or use simple http call implementation an implement 
   an http request enhancer)
 - implement test result listener for reporting results (or use simple report 
   listener)
 - start tests   

~~~java
 tests.enhancer(addauthenhancer)
     .processTests(
     request -> SimpleHttpCall.instance().call(request) // http caller
     , summary, report); // add report data containers
~~~

 - reads tests result from listeners

You can find simplified applications [here](./src/test/java/example).


## logging

 Framework uses slf4j for internal logging, so configure it as you need. 
 
 If you enable debug level on 'sk.antons.resttests.tests.requests' you 
 will find each test result as json logged. (with resulting condition)

~~~json
{
  "category" : "/home/projects/my/antonsjava/rest-tests/./src/test/example/template",
  "name" : "simple-post-json-test.tmpl",
  "result" : "SUCCESSFUL",
  "request" : {
    "url" : "https://echo.zuplo.io",
    "method" : "POST",
    "header" : {
      "Content-Type" : "application/json"
    },
    "payload" : {
      "id" : "1681761844846",
      "name" : "John-1681761844845",
      "surname" : "Smith-1681761844845",
      "job" : "designer",
      "book" : {
        "isbn" : "9781593277574",
        "title" : "Understanding ECMAScript 6",
        "subtitle" : "The Definitive Guide for JavaScript Developers",
        "author" : "Nicholas C. Zakas"
      }
    }
  },
  "condition" : {
    "status" : {
      "eq" : "200",
      "value" : "200",
      "result" : true
    },
    "body.body.id" : {
      "not empty" : "",
      "value" : "1681761844846",
      "result" : true
    },
    "body.body.book.author" : {
      "not empty" : "",
      "value" : "Nicholas C. Zakas",
      "result" : true
    }
  },
  "response" : {
    "status" : 200,
    "header" : {
      "access-control-allow-credentials" : "true",
      "access-control-allow-headers" : "*",
      "access-control-allow-methods" : "GET,HEAD,POST,PUT,DELETE,CONNECT,OPTIONS,TRACE,PATCH",
      "access-control-allow-origin" : "*",
      "access-control-expose-headers" : "*",
      "access-control-max-age" : "600",
      "connection" : "keep-alive",
      "content-length" : "1225",
      "content-type" : "application/json",
      "date" : "Mon, 17 Apr 2023 20:04:05 GMT",
      "server" : "cloudflare"
    },
    "body" : "{\n  \"url\": \"https://echo.zuplo.io/\",\n  \"method\": \"POST\",\n  \"query\": {},\n  \"body\": {\n    \"id\": \"1681761844846\",\n    \"name\": \"John-1681761844845\",\n    \"surname\": \"Smith-1681761844845\",\n    \"job\": \"designer\",\n    \"book\": {\n      \"isbn\": \"9781593277574\",\n      \"title\": \"Understanding ECMAScript 6\",\n      \"subtitle\": \"The Definitive Guide for JavaScript Developers\",\n      \"author\": \"Nicholas C. Zakas\" },\n  \"headers\": {\n    \"accept-encoding\": \"gzip\",\n    \"connection\": \"Keep-Alive\",\n    \"content-length\": \"697\",\n    \"content-type\": \"application/json\",\n    \"host\": \"echo.zuplo.io\",\n    \"true-client-ip\": \"178.41.237.249\",\n    \"user-agent\": \"Java-http-client/17.0.5\",\n    \"x-forwarded-proto\": \"https\",\n    \"x-real-ip\": \"178.41.237.249\"\n  }\n}"
  }
}
~~~

 If you use SimpleHttpCall and enable debug level on 'sk.antons.resttests.http.call' 
 you will find each http request as text logged. 


## Maven usage

If you want to build your own application

```
   <dependency>
      <groupId>io.github.antonsjava</groupId>
      <artifactId>rest-tests</artifactId>
      <version>LATESTVERSION</version>
   </dependency>
```

If you want to use Application which 

 - has no authentication
 - print result to console

You can clone this project and build single jar application rest-test.jar 

~~~
mvn clean package -P application
~~~

and than you can use 

~~~
java -jar rest-tests.jar [-l log-file-name] [-d] 
          [-p name=value]* [-s resource-url]* 
          [-r report-file-name]  
          test-root-directory
   [-l|--log log-file-name] - prints log to file instead of console 
   [-d|--debug] - prints debug log output 
   [-p|--param name=value]* - define properties for placeholder replacements
   [-s|--source resource-url]* - define source urls
   [-r|--report report-file-name] - define output for text report (normaly it is printed only to console) 
   [-inc|--include file-path-pattern] - file matcher used for directory scan (like **/*-test.json) 
   [-exc|--exclude file-path-pattern] - file matcher used for directory scan (like **/prod/**) 
   [-se|--sourceEnc encoding] - source file encoding 
   [-ue|--urlEnc encoding] - url file encoding 
   [-f|--file|--directory file/direcotry] - root for test search
~~~

for example

~~~
java -jar ./target/rest-tests.jar -f src/test/example/  -inc '**/*-test.json' -r /tmp/report.txt -l /tmp/report.log 
~~~

