# Trivial Web Crawler
Trivial Java Web Crawler. 

Extremely simple web crawler which will connect to specified 
web site and create simplified site map. To run it just 

## Getting Started

It is built with [maven](http://maven.apache.org) and 
requires [Java 8](http://java.oracle.com). 

To build clone repository and run `mvn package`; the "fat", runnable 
jar will be created in `./target/crawler-1.0-SNAPSHOT-jar-with-dependencies.jar`.

The application can be run like this 
```
java -jar ./target/crawler-1.0-SNAPSHOT-jar-with-dependencies.jar domain
```
where `domain`  is http address of the resource to be crawled.

All discovered resources are grouped into 3 categories
- resources local to given domain
- resources in other domains 
- urls to discovered images.

Discovered links are saved into following 3 files in the working directory:
- resources local to given domain: local.txt
- resources non local to given domain: remote.txt
- discovered images: images.txt

The code is not unit tested as it is mainly integration of
[HttpClient](https://hc.apache.org/httpcomponents-client-ga/)
and [jsoup](https://jsoup.org/) but it should be (mocking all 
needed objects will take more than writing the application itself).

## TODO 
* simplify, there is hardly any reason for use of HTML parser
* discover more link types 
* clean code 
* add tests 

## LICENCE
This is free and unencumbered software released into the public domain.

Anyone is free to copy, modify, publish, use, compile, sell, or
distribute this software, either in source code form or as a compiled
binary, for any purpose, commercial or non-commercial, and by any
means.

In jurisdictions that recognize copyright laws, the author or authors
of this software dedicate any and all copyright interest in the
software to the public domain. We make this dedication for the benefit
of the public at large and to the detriment of our heirs and
successors. We intend this dedication to be an overt act of
relinquishment in perpetuity of all present and future rights to this
software under copyright law.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

For more information, please refer to <http://unlicense.org/>
