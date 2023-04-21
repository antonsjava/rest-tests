# request

Following parts can be defined for request 

~~~json
{
  ...
  "request" : {
    "url" : ...
    "method" : ...
    "query" : ...
    "header" : ...
    "payload": ...
    "payloadAsString": ...
    "payloadFromResource": ...
    "payloadFromQuery": ...
  },
  ...
}
~~~


# url

 Url is defined by string literal. It must be valid url with schema.
 
 If url contains also query part it can be defined in separate definition part


~~~json
{
  "url" : "http://localhost:8080"
}
~~~

# method

 Url is defined by string literal. It can contain GET, HEAD, POST, PUT, DELETE
 
~~~json
{
  "method" : "POST"
}
~~~

# query

 Query ca define additional 'query' part to url. (or in special case to body)
 It is defined by simple json object with set of string literal attributes.
 
~~~json
{
  "query" : {
     "param1": "value1",
     "param2": "value2",
     "param3": "value3"
  }
}
~~~

# header

 HEAD ca define additional 'header' part to http request.
 It is defined by simple json object with set of string literal attributes.
 
~~~json
{
  "header" : {
     "name1": "value1",
     "name2": "value2",
     "name3": "value3"
  }
}
~~~

# payload all

 Payload part define request body. Payload has more forms. If it is necessary to define payload you must use one of them

## payload

 In this case the http request body is json value defined by 'payload' attribute.
 As rest APIs usually are json based - this way is mostly used

~~~json
{
  "payload" : {
     "id": "1231",
     "name": "Shogun",
     "author": "James Clavell"
  }
}
~~~

## payloadAsString

 In this case the http request body is string defined by json 'payloadAsString' attribute.
 The text from attribute will be sent as is.

~~~json
{
  "payloadAsString" : "QSBzZWNyZXQgc3RyaW5nCg=="
}
~~~


## payloadFromQuery

 In this case the http request body is constructed from query part. (in this case query part is not addet to url)
 Value is ignored define just true literal to make json correct
 It is used for simulating html form posts and you shoul add also right content type.


~~~json
{
  "query" : {
     "param1": "value1",
     "param2": "value2",
     "param3": "value3"
  },
  "header" : {
     "Content-Type": "application/x-www-form-urlencoded"
  },
  "payloadFromQuery" : true
}
~~~

## payloadFromResource

 In this case the http request body is constructed as text from specified resource.

~~~json
{
  "payloadFromResource" : "./body/body-example.txt"
}
~~~
