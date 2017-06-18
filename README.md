# Trivial Web Crawler
Python web crawler written in less than an hour.

## Getting Started
Extremelly simple web crawler which will connect to specified 
web site and create simplified site map. To run it just 
clone reposiory or download 'crawler.py' and run it like 
'''
python crawler.py example.com
'''
to generate simplified site map of http://example.com. Only resources on the initial domain 
are visited by this crawler. The output is a file 'a-site-map.txt' in which  
all discovered resources are grupped in 3 following categories 
- resources local to given domain
- resources in other domains (EXTERNAL SITE LINKED FROM)
- urls to discovered images (IMAGES)

## TODO 
* create better site map
* discover more link types 
* clean code 
* add tests !!!!!

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
