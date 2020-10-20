import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;

public class Factorizer {

	public Resource uri;
	public static Model reducedModel = ModelFactory.createDefaultModel();
	public static Model originalModel = ModelFactory.createDefaultModel();
	public static HashMap<measurementClass, Resource> mapMeasurement = new HashMap<measurementClass, Resource>();
	public static HashMap<observationClass, Resource> mapObservation = new HashMap<observationClass, Resource>();
	public static Map<Boolean, Resource> mapTruth = new HashMap<Boolean, Resource>();
	public static long originalTriples = 0;
	public static long factorizedTriples = 0;
	

	public static void main(final String[] args) throws ParseException,
			IOException {
	final String path_to_original_data = args[0]; // Path to RDF data to be factorized.
	final String path_to_reduced_data = args[1]; // Path to RDF data previously factorized.
	final String path_to_observation_hashmap = args[2]; // Path to the file, along with file name, containing observation mappings.
	final String path_to_measurement_hashmap = args[3]; // Path to the file, along with file name, containing measurement mappings.
			
			
	File obsDir = new File(path_to_observation_hashmap);  
	if(obsDir.exists()) { 
		// If obsDir contains the file with the observation mappings, then read those observation mappings. Otherwise do nothing.
		System.out.println("arg 2 : "+path_to_observation_hashmap);
		ReadHashmap("Observation",path_to_observation_hashmap); 
		} 
	
	File measDir = new File(path_to_measurement_hashmap); 
	if(measDir.exists()) {
		// If measDir contains the file with the measurement mappings, then read those measurement mappings. Otherwise do nothing.
		System.out.println("arg 3 : "+ path_to_measurement_hashmap);
		ReadHashmap("Measurement",path_to_measurement_hashmap); 
	} 
	
	final long startTime = System.currentTimeMillis();  
	System.out.println("Data Factorization Started.....");
	singleFileFactorization( path_to_original_data, path_to_reduced_data); 
	final long endTime = System.currentTimeMillis(); 
	final long totalTime = endTime - startTime; 
	System.out.println("totalTime........." + totalTime +" ms");
	System.out.println("Factorization done! Now writing Maps.");
	writeHashmap(path_to_observation_hashmap, path_to_measurement_hashmap);
		 
		
	}

	
	
	public static void ReadHashmap(String tag, String path_to_hashmap_file) throws IOException {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Resource.class,
                new JenaResourceConverter.Serializer());
        builder.registerTypeAdapter(Resource.class,
                new JenaResourceConverter.Deserializer());
		Gson gson = builder.excludeFieldsWithoutExposeAnnotation().enableComplexMapKeySerialization().setPrettyPrinting().create();
		
       
		if (tag == "Observation") {
			Type type = new TypeToken<HashMap<observationClass, Resource>>(){}.getType();
			 String jsonReadString ="";
		        try 
		        {	 
		        System.out.println("Reading Observation JSON Hashmap file from Java program"); 
		        jsonReadString = readFileAsString(path_to_hashmap_file);
		        System.out.println(jsonReadString);
		        } 
		        catch (Exception ex) { ex.printStackTrace(); }

		        System.out.println("\nAfter deserialization:"); 
		        mapObservation = gson.fromJson(jsonReadString, type);
				  
				/*
				 * for (observationClass t3 : mapObservation.keySet()) {
				 * System.out.println("sensor: "+t3.getSensor());
				 * System.out.println("getProperty: "+t3.getProperty());
				 * System.out.println("getPhenomena: "+t3.getPhenomenon());
				 * System.out.println("getmURI: "+t3.getmURI()); System.out.println("\t" +
				 * mapObservation.get(t3)); }
				 */
		}
		else if (tag == "Measurement") {
			Type type = new TypeToken<HashMap<measurementClass, Resource>>(){}.getType();
			 String jsonReadString ="";
		        try 
		        {
		        	 
		        System.out.println("Reading Measurement JSON Hashmap file from Java program"); 
		        jsonReadString = readFileAsString(path_to_hashmap_file);
		        System.out.println(jsonReadString);
		        } 
		        catch (Exception ex) { ex.printStackTrace(); }

		        System.out.println("\nAfter deserialization:"); 
		        mapMeasurement = gson.fromJson(jsonReadString, type);
				  
				/*
				 * for (measurementClass t3 : mapMeasurement.keySet()) {
				 * System.out.println("sensor: "+t3.getValue());
				 * System.out.println("getProperty: "+t3.getUOM()); }
				 */
		}
	}
	
	public static void writeHashmap(String path_to_observationMap, String path_to_measurementMap ) throws IOException {
		
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Resource.class,
                new JenaResourceConverter.Serializer());
        builder.registerTypeAdapter(Resource.class,
                new JenaResourceConverter.Deserializer());
		Gson gson = builder.excludeFieldsWithoutExposeAnnotation().enableComplexMapKeySerialization().setPrettyPrinting().create();

       
        Type otype = new TypeToken<HashMap<observationClass, Resource>>(){}.getType();
        String json = gson.toJson(mapObservation, otype);
        //System.out.println("gson: "+json);
        try { System.out.println("Writting observation JSON into file ...");
        //System.out.println(json); 
        FileWriter jsonFileWriter = new FileWriter(path_to_observationMap); 
        jsonFileWriter.write(json); 
        jsonFileWriter.flush(); 
        jsonFileWriter.close(); 
        System.out.println("Done writing Observation Hashmap"); 
        json = "";
        mapObservation.clear();
        } 
        catch (IOException e) { e.printStackTrace(); }

        
        Type mtype = new TypeToken<HashMap<measurementClass, Resource>>(){}.getType();
        String mjson = gson.toJson(mapMeasurement, mtype);
        //System.out.println("gson: "+mjson);

        try { 
        System.out.println("Writting measurement JSON into file ...");
        //System.out.println(mjson); 
        FileWriter jsonFileWriter = new FileWriter(path_to_measurementMap); 
        jsonFileWriter.write(mjson); 
        jsonFileWriter.flush(); 
        jsonFileWriter.close(); 
        System.out.println("Done writing Measurement Hashmap"); 
        mapMeasurement.clear();
        mjson="";
        } 
        catch (IOException e) { e.printStackTrace(); }

}
	
	public static String readFileAsString(String file)throws Exception
    {
        return new String(Files.readAllBytes(Paths.get(file)));
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
			//originalModel.read(in, null, "N3");
			System.out.println("Reading Triples ..."+inputFileName);
			//originalModel.read(in, null, "Turtle");
			originalModel.read(inputFileName);
			System.out.println("Read Triples ...");
			originalTriples = originalTriples +  originalModel.size();

			final long startTime = System.currentTimeMillis();
			FloatFactorizer.factorize();
			TruthFactorizer.factorize();
			final long endTime = System.currentTimeMillis();
			final long totalTime = endTime - startTime;
			factorizationTime = factorizationTime + totalTime;
			originalModel.removeAll();
			final OutputStream out = new FileOutputStream(path_to_reduced_data
					+ outputFileName);
			factorizedTriples =factorizedTriples + reducedModel.size();
			reducedModel.write(out, "TTL");
			reducedModel.removeAll();
			out.close();
			in.close();
		}
		System.out.println("factorizationTime........." + factorizationTime+" ms");
		System.out.println("Triples Before factorization........." + originalTriples);
		System.out.println("Triples After factorization........." + factorizedTriples);
		
	}

}
