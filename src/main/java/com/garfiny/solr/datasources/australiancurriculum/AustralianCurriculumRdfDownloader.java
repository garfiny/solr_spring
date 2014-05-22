package com.garfiny.solr.datasources.australiancurriculum;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.*;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:australian_curriculum_rdf.properties")
public class AustralianCurriculumRdfDownloader {

	public static final Logger LOG = Logger
			.getLogger(AustralianCurriculumRdfDownloader.class);

	private static final String ENGLISH_RDF_FILENAME = "english.rdf";

	@Value("${australian_curriculum_sparql_endpoint}")
	private Resource accSparqlUrl;

	@Value("classpath:${english_query}")
	private Resource englishQuery;

	@Value("${download_target_dir}")
	private Resource downloadTargetDir;

	public Resource getAccSparqlUrl() {
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
		InputStream inputStream = null;
		BufferedWriter writer = null;
		try {
			URI uri = new URI(null, null, accSparqlUrl.getURI().toString(),
					"query=" + sparqlQueryToString(englishQuery), null);
			URLConnection connection = uri.toURL().openConnection();
			connection.setRequestProperty("Accept", "application/rdf+xml");
			
			Path path = Paths.get(downloadTargetDir.getURI());
			Path file = path.resolve(ENGLISH_RDF_FILENAME);
			Files.deleteIfExists(file);
			writer = Files.newBufferedWriter(Files.createFile(file), CREATE);
			inputStream = connection.getInputStream();
			IOUtils.copy(inputStream, writer, "UTF-8");
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}finally {
			try {
				inputStream.close();
				writer.close();
			}catch(IOException ix) {
				ix.printStackTrace();
			}
		}
	}
}