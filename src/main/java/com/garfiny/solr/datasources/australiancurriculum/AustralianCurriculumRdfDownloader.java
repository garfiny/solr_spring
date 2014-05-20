package com.garfiny.solr.datasources.australiancurriculum;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class AustralianCurriculumRdfDownloader {

	public static final Logger LOG = Logger
			.getLogger(AustralianCurriculumRdfDownloader.class);

	private static final String ENGLISH_RDF_FILENAME = "english.rdf";

	@Value("${australian_curriculum_sparql_endpoint}")
	private String accSparqlUrl;

	@Value("classpath:${english_query}")
	private Resource englishQuery;

	@Value("${download_target_dir}")
	private String downloadTargetDir;

	public String getAccSparqlUrl() {
		return accSparqlUrl;
	}

	public Resource getEnglishQuery() {
		return englishQuery;
	}
	
	private String sparqlQueryToString(Resource queryFile) {
		try(InputStream is = queryFile.getInputStream()) {
			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer, "UTF-8");
			return writer.toString();
		} catch (IOException e) {
			return "";
		}
	}

	public void downloadEnglish() {
		URLConnection connection = null;
		try {
			URI uri = new URI(null, null, accSparqlUrl,
					"query=" + sparqlQueryToString(englishQuery), null);
			connection = uri.toURL().openConnection();
			connection.setRequestProperty("Accept", "application/rdf+xml");
		} catch (URISyntaxException | IOException e) {
		}
		try (InputStream inputStream = connection.getInputStream();
				Writer writer = new FileWriter(new File(String.format("%s/%s",
						downloadTargetDir, ENGLISH_RDF_FILENAME)))) {
			IOUtils.copy(inputStream, writer, "UTF-8");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}