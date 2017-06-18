#!/usr/bin/env python
from __future__ import print_function
import sys
import urllib2
import urlparse
import logging

from HTMLParser import HTMLParser


logging.basicConfig(filename='example.log', level=logging.DEBUG)


class Parser(HTMLParser):
    def __init__(self, links, images):
        HTMLParser.__init__(self)
        self.__links = links
        self.__images = images

    def handle_starttag(self, tag, attrs):
        def get_attr(attr_name):
            for a in attrs:
                if a[0].lower() == attr_name:
                    return a[1].lower()
            raise ValueError(attr_name + ' not present in ' + tag)

        t = tag.lower()
        try:
            if t in ('link', 'a'):
                self.__links.append(get_attr('href'))
            elif t in ('script'):
                self.__links.append(get_attr('src'))
            elif t in ['img']:
                self.__images.add(get_attr('src'))
        except ValueError as ex:
            logging.debug(ex.message)


def print_message(msg):
    sys.stdout.write('\r{0}'.format(msg))
    sys.stdout.flush()

    
if __name__ == '__main__':
    crawled_domain = sys.argv[1]
    if not crawled_domain:
        raise ValueError('domain to crawl is mandatory')

    links = ['http://' + crawled_domain]
    processed = set()
    images = set()
    external_links = set()
    html_parser = Parser(links, images)

    while links:
        current_url = links.pop()
        
        if current_url in processed:
            continue
        
        logging.debug('processing %s', current_url)
        
        url = urlparse.urlparse(current_url)
        if url.hostname and url.hostname != crawled_domain:
            # external link do not visit
            external_links.add(current_url)
            continue
        processed.add(current_url)

        try:
            print_message("processing {}".format(current_url))
            response = urllib2.urlopen(current_url)
            body = response.read()
            html_parser.feed(body)
        except urllib2.URLError as ex:
            logging.warning('failed to open %s. %s', current_url, ex.message)
            continue
        except ValueError as ex:
            logging.debug('malformed url %s. %s', current_url, ex.message)
            continue

    with file('a-site-map.txt', 'w') as out:
        for link in processed:
            print(link, file=out)
        print('EXTERNAL SITE LINKED FROM  ' + crawled_domain, file=out)
        for link in external_links:
            print(link, file=out)
        print('IMAGES FOUND', file=out)
        for link in images:
            print(link, file=out)
