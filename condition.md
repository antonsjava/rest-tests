# condition

Condition is used for evaluatong http response. Each test define one condition (which can be complex). 
If condition evaluation fail test is marked as failed.

## text resolver

Conditions finaly call 'checks' which accept text value and validate that value. Such texts are resolved 
from http response. Framework provide TextResolver as generic mechanizm for resolving texts from response 
using string value called locator. 

In your application you can implement own resolving. But framework by default provide implementation, that 
accelt following locators

 - 'status' - identify response status
 - 'header.header-name' - identifies header with name value if there are more headers with that name they are returned as one string coma separated.
 - 'body' - text of response body
 - 'body.att1.att2.att3' - interprets body as json and use following path to determine subset of body.

Default text resolver allows to add to selector text comma separated list of enhancers to format resulted text
Following enhancers are supported
 
 - 'trim' - to trim spaces from beginning and end of string.
 - 'tolower' - to lowercase string.
 - 'toupper' - to uppercase string.
 - 'ascii' - to convert string to ascii.

so you can use selectors like 'body:tolower', 'header.header-name:trim,ascii.tolower'.


## conditions

There are following types of condition

 - root condition - combine other conditions with and boolean operator
 - selector condition - main condition type for checking response data
 - and condition - combine other conditions with 'and' boolean operator 
 - or condition - combine other conditions with 'or' boolean operator 
 - not condition - negate result of other condition

### root condition

This is mandatory type for root condition. It is json object where each attribute is
individual condition and all must be satisfied. 

~~~json
{
  ...
  "condition" : {
    "cond1" : ...
    "cond2" : ...
    "cond3" : ...
  },
  ...
}
~~~

### selector condition

This is main type of condition. It has two parts 

 - selector - text which is used for selecting string from response 
 - check - json object which define check for selected text value

Selector condition is json attribute inside root condition.

~~~json
{
  ...
  "condition" : {
    "status" : ...
    "header.content-type" : ...
    "body" : ...
    "body.book.author" : ...
  },
  ...
}
~~~

Read rest of this document to see how to define checks.

### and condition

This is helper type of condition. It cumulates more conditions and ensures 
that all are satisfied. Nested conditions are listed in json array.

~~~json
{
  ...
  "condition" : {
    "and" : [{}, {}, ...]
  },
  ...
}
~~~

### or condition

This is helper type of condition. It cumulates more conditions and ensures 
that at least one is satisfied. Nested conditions are listed in json array.

~~~json
{
  ...
  "condition" : {
    "or" : [{}, {}, ...]
  },
  ...
}
~~~

### not condition

This is helper type of condition. It negate other condition.
Nested condition is defined by json object.

~~~json
{
  ...
  "condition" : {
    "not" : {},
  },
  ...
}
~~~

## checks

Check is usually defined using json object with exactly one attribute. Name of attribute 
determine check type and value is used for check evaluation. There are also container 
check which allows to define more complex checks.

All simple checks ( not 'and', 'or' and 'not') can be prefixed with 'not ' to negate value.

### and check

Define 'and' for other checks. Other checks are listed in json array. 

~~~json
{ "and": [{}, {}, {}, ....]}
~~~

### or check

Define 'or' for other checks. Other checks are listed in json array. 

~~~json
{ "or": [{}, {}, {}, ....]}
~~~

### not check

Define 'not' for other check. 

~~~json
{ "not": {}}
~~~

### eq check

true if given string is equal to provided text value. 

~~~json
{ "eq": "value to check with given text"}
~~~

### contains check

true if given string is substring of provided text value. 

~~~json
{ "contains": "substring of text"}
~~~

### contains check

true if given regexp matches provided text value. 

~~~json
{ "match": "regexp"}
~~~

### empty check

true provided text value is null of empty. Check not require any configuration
so just provide true literal to ensure correct json format.

~~~json
{ "empty": true}
~~~

### ends check

true if given string is end of provided text value. 

~~~json
{ "ends": "end substring of text"}
~~~

### starts check

true if given string is start of provided text value. 

~~~json
{ "starts": "beginning substring of text"}
~~~

### gt check

true if given string interpreted as number is lower or equal to provided text value 
interpreted as number. (if one of numbers contain '.' they are interpreted as double)

~~~json
{ "gt": "10"}
~~~

### gte check

true if given string interpreted as number is lower to provided text value 
interpreted as number. (if one of numbers contain '.' they are interpreted as double)

~~~json
{ "gte": "10"}
~~~


### lt check

true if given string interpreted as number is greater or equal to provided text value 
interpreted as number. (if one of numbers contain '.' they are interpreted as double)

~~~json
{ "lt": "10"}
~~~

### lte check

true if given string interpreted as number is greater to provided text value 
interpreted as number. (if one of numbers contain '.' they are interpreted as double)

~~~json
{ "lte": "10"}
~~~



