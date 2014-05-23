package com.garfiny.solr.datasources.australiancurriculum;

import static java.nio.file.StandardOpenOption.CREATE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.DC_11;
import com.hp.hpl.jena.vocabulary.DCTerms;

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
	
	@Value("${english_resource_uri}")
	private Resource englishResourceUri;

	public void downloadEnglish() {
		try (InputStream is = createConnection().getInputStream()) {
			Model model = ModelFactory.createDefaultModel();
			model.read(is, null);
			saveToDisk(model);
			standardDocument(model);
		}catch(URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void standardDocument(Model model) {
		com.hp.hpl.jena.rdf.model.Resource resource = model.getResource(englishResourceUri.toString());
		Property toc = model.getProperty(DCTerms.tableOfContents.toString());
		Statement stmt = model.getRequiredProperty(null, toc);
		System.out.println(stmt.getObject().toString());
		donwloadTableOfContents(stmt.getObject().toString());
//		StmtIterator itr = model.listStatements(new SimpleSelector(resource, null, (RDFNode)null));
//		StmtIterator itr = model.listStatements(new SimpleSelector(null, toc, (RDFNode)null));
//		int i = 0;
//		while (itr.hasNext()) {
//			i++;
//		    Statement stmt      = itr.nextStatement();  // get next statement
//		    printStatement(stmt);
//		}
//		System.out.println("Total statements: " + i);
	}
	
	private void donwloadTableOfContents(String resourceUrl) {
		try (BufferedReader reader = 
				new BufferedReader(new InputStreamReader(
						new URL(resourceUrl).openStream()))) {
//			JsonReader jsonReader = new JsonReader(reader);
			IOUtils.copy(reader, System.out);
		} catch(IOException ix) {
			ix.printStackTrace();
		}
	}
	
	private void printStatement(Statement stmt) {
		com.hp.hpl.jena.rdf.model.Resource subject = stmt.getSubject(); // subject
		Property predicate = stmt.getPredicate(); // get the predicate
		RDFNode object = stmt.getObject(); // get the object
		System.out.print(subject.toString());
		System.out.print(" == " + predicate.toString() + " == ");
		if (object instanceof Resource) {
			System.out.print(object.toString());
		} else {
			System.out.print(" \"" + object.toString() + "\"");
		}
		System.out.println(" .");
	}
	
	private URLConnection createConnection() throws IOException, URISyntaxException {
		URI uri = new URI(null, null, accSparqlUrl.getURI().toString(),	
				"query=" + sparqlQueryToString(englishQuery), null);
		URLConnection connection = uri.toURL().openConnection();
		connection.setRequestProperty("Accept", "application/rdf+xml");
		return connection;
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
	
	private void saveToDisk(Model model) throws IOException {
		Path path = Paths.get(downloadTargetDir.getURI());
		Path file = path.resolve(ENGLISH_RDF_FILENAME);
		Files.deleteIfExists(file);
		try (BufferedWriter writer = 
				Files.newBufferedWriter(Files.createFile(file), CREATE)) {
			model.write(writer);
		}catch(IOException ix) { throw ix;}
	}
	
	public Resource getAccSparqlUrl() {
		return accSparqlUrl;
	}

	public Resource getEnglishQuery() {
		return englishQuery;
	}
}