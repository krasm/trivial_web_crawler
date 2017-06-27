package net.krasm;

import java.util.*;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * simple storage of visited URLs
 */
class URIStorage {
	private static final Logger LOGGER = Logger.getLogger(URIStorage.class);
	private static final String LOCAL_LINKS_FILE_NAME = "local.txt";
	private static final String REMOTE_LINKS_FILE_NAME = "remote.txt";
	private static final String IMAGE_LINKS_FILE_NAME = "images.txt";

    private final String baseDomain;
	private Set<String> local;
	private Set<String> remote;
	private Set<String> images;

    public URIStorage(final String baseDomain) {    
        this.baseDomain = baseDomain;
        this.local = new HashSet<>();
        this.remote = new HashSet<>();
        this.images = new HashSet<>();
    }


    public boolean isLocal(final String url) {
        try {
            final URL u = new URL(url);
            return baseDomain.equals(u.getHost());
        } catch(MalformedURLException ex) {
            LOGGER.debug("malformed url " + ex.getMessage());
            return false;
        }
    }   

    public boolean isVisited(final String url) {
        return local.contains(url);
    }

    /**
     * not good as I returning boolean depends on the link type
     */
    public boolean addLink(final String url) {
        if(isLocal(url)) {
            LOGGER.info("adding local URL " + url);
            local.add(url);
            return true;
        } else {
            LOGGER.info("adding remote URL " + url);
            remote.add(url);
        }

        return false;
    }

    public void addStaticContentRef(final String url) {
        images.add(url);
    }

    public String getBaseDomain() {
        return this.baseDomain;
    }

	public void serializeResults() throws IOException {
		printCollectionToFile(local, LOCAL_LINKS_FILE_NAME);
		printCollectionToFile(remote, REMOTE_LINKS_FILE_NAME);
		printCollectionToFile(images, IMAGE_LINKS_FILE_NAME);
	}
	
	private void printCollectionToFile(final Collection<String> c, final String fileName) throws IOException {
		PrintWriter out = new PrintWriter(fileName);
		for(String l : c) {
			out.println(l);
		}
		out.close();	
	}
};

public class Main implements Closeable, Runnable {

	private static final Logger LOGGER = Logger.getLogger(Main.class);

	private CloseableHttpClient client;
    private final URIStorage uriStorage;
	private final String initialUrl;
	
	private Main(final  String initialUrl) throws MalformedURLException {
        this.initialUrl = initialUrl;
		final String baseHost = new URL(initialUrl).getHost();
        this.uriStorage = new URIStorage(baseHost);
	}

	/**
	 * Simple serialization of the crawling
	 * 
	 * Create 3 files: 
	 * <ul>
	 * <li><code>local.txt</code> with links to resources in the same domain</li>
	 * <li><code>remote.txt</code> with links to resources in remote systems</li>
	 * <li><code>images.txt</code> with link to images found</li>
	 * @throws IOException 
	 * 
	 */
	
	@Override
	public void close() throws IOException {
		if (client != null) {
			client.close();
		}
	}

	@Override
	public void run() {
		client = HttpClients.createDefault();
		Queue<String> q = new LinkedList<>();
		q.add(initialUrl);
		try {
			while (!q.isEmpty()) {
				final String current = q.remove();
				if (LOGGER.isInfoEnabled())
					LOGGER.info("processing " + current);
                
                // FIXME ugly 
                final boolean skipCurrentUrl = !uriStorage.isLocal(current) || uriStorage.isVisited(current);
                uriStorage.addLink(current);
                if(skipCurrentUrl) 
                    continue;

				CloseableHttpResponse response = null;
				try {
					HttpGet request = new HttpGet(current);					
					response = client.execute(request);
                    Collection<String> links = processResponse(response);

                    for(final String l : links) 
                        if(!uriStorage.isVisited(l))
                            q.add(l);
				} catch (IOException | IllegalArgumentException ex) {
					LOGGER.warn("failed to process " + current, ex);
				} finally {
                    // FIXME make it helper function 
					if (response != null)
						try { 
							response.close();
						} catch(IOException ex) {
							LOGGER.warn("failed to close response ", ex);
						}
				}
			}
		} catch (NoSuchElementException ex) {
			LOGGER.info("crawling finished");
		}
	}

	/**
	 * Process HTTP response.
	 *
	 * @param response
	 */
	private Collection<String> processResponse(HttpResponse response) {
		final int code = response.getStatusLine().getStatusCode();

		if (code / 100 == 2) {
			try {
				Document doc = Jsoup.parse(response.getEntity().getContent(), null, initialUrl);
				List<String> links = processLinks(new Elements[] { doc.getElementsByTag("a") });
				processStaticContent(new Elements[] { doc.getElementsByTag("img") } );
                LOGGER.info("retrievied " + links.size() + " links");
                return links;
			} catch (IOException ex) {
				LOGGER.warn("failed to process response", ex);
			}
		}

        return Collections.EMPTY_LIST;
	}

	private List<String> processLinks(Elements[] links) {
		List<String> ret = new ArrayList<>();
		for (Elements elements : links) {
			for (Element link : elements) {

				final String href = link.attr("href").toLowerCase();
                ret.add(href);
			}
		}

		return ret;
	}

	private void processStaticContent(Elements[] links) {		
		for (Elements elements : links) {
			for (Element link : elements) {
				final String href = link.attr("src").toLowerCase();
				LOGGER.debug("retrievied image src " + href);
                uriStorage.addStaticContentRef(href);
			}
		}
	}

    public void serializeResults() throws IOException {
        uriStorage.serializeResults();
    }

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("usage");
			System.exit(-1);
		}

		Main m = new Main(args[0]);
		try {
			m.run();
			m.serializeResults();
		} finally {
			m.close();
		}

	}
}
