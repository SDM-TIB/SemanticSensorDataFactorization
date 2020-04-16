import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;

public class Factorizer {

	public static Model reducedModel = ModelFactory.createDefaultModel();
	public static Model originalModel = ModelFactory.createDefaultModel();
	public static HashMap<measurementClass, Resource> mapMeasurement = new HashMap<measurementClass, Resource>();
	public static HashMap<observationClass, Resource> mapObservation = new HashMap<observationClass, Resource>();
	public static Map<Boolean, Resource> mapTruth = new HashMap<Boolean, Resource>();

	public static void main(final String[] args) throws ParseException,
			IOException {
		final long startTime = System.currentTimeMillis();
		final String path_to_original_data = args[0];
		final String path_to_reduced_data = args[1];
		System.out.println("factorizing Data.....");
		singleFileFactorization(
				path_to_original_data,
				path_to_reduced_data);
		final long endTime = System.currentTimeMillis();
		final long totalTime = endTime - startTime;
		System.out.println("totalTime........." + totalTime);
	}

	public static void singleFileFactorization(
			final String path_to_original_data,
			final String path_to_reduced_data) throws ParseException,
			IOException {
		long factorizationTime = 0;
		String inputFileName = "", outputFileName = "";
		final File folder = new File(path_to_original_data);
		final File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			reducedModel
					.setNsPrefix("om-owl",
							"http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#");
			reducedModel.setNsPrefix("rdfs",
					"http://www.w3.org/2000/01/rdf-schema#");
			reducedModel.setNsPrefix("sens-obs",
					"http://knoesis.wright.edu/ssw/");
			reducedModel.setNsPrefix("owl-time",
					"http://www.w3.org/2006/time#");
			reducedModel.setNsPrefix("owl",
					"http://www.w3.org/2002/07/owl#");
			reducedModel.setNsPrefix("xsd",
					"http://www.w3.org/2001/XMLSchema#");
			reducedModel.setNsPrefix("weather",
					"http://knoesis.wright.edu/ssw/ont/weather.owl#");
			reducedModel.setNsPrefix("rdf",
					"http://www.w3.org/1999/02/22-rdf-syntax-ns#");
			reducedModel.setNsPrefix("lld",
					"http://linkeddata.com/ontology#");
			inputFileName = outputFileName = listOfFiles[i].getName();
			inputFileName = path_to_original_data + inputFileName;
			final InputStream in = FileManager.get().open(inputFileName);
			if (in == null) { throw new IllegalArgumentException("File: " + inputFileName
					+ " not found"); }
			originalModel.read(in, null, "N3");
			final long startTime = System.currentTimeMillis();
			FloatFactorizer.factorize();
			TruthFactorizer.factorize();
			final long endTime = System.currentTimeMillis();
			final long totalTime = endTime - startTime;
			factorizationTime = factorizationTime + totalTime;
			originalModel.removeAll();
			final OutputStream out = new FileOutputStream(path_to_reduced_data
					+ outputFileName);
			reducedModel.write(out, "N3");
			reducedModel.removeAll();
			out.close();
			in.close();
		}
		System.out.println("factorizationTime........." + factorizationTime);
	}

}
