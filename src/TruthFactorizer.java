import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class TruthFactorizer {

	public static void factorize() throws ParseException, IOException {

		final ResultSet results = getTruthObservation();
		while (results.hasNext()) {
			final QuerySolution qs = results.nextSolution();
			Factorizer.reducedModel
					.add(qs.getResource("ob"),
							Factorizer.reducedModel
									.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#result"),
							qs.getResource("result"));
			Factorizer.reducedModel
					.add(qs.getResource("ob"),
							Factorizer.reducedModel
									.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#samplingTime"),
							qs.getResource("samplingTime"));
			Factorizer.reducedModel
					.add(qs.getResource("samplingTime"),
							Factorizer.reducedModel
									.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
							Factorizer.reducedModel
									.createResource("http://www.w3.org/2006/time#Instant"));
			Factorizer.reducedModel
					.add(qs.getResource("samplingTime"),
							Factorizer.reducedModel
									.createProperty("http://www.w3.org/2006/time#inXSDDateTime"),
							qs.getLiteral("time"));
			final String split[] = qs.get("v").toString().split("\\^");

			createMolecules(Boolean.valueOf(split[0]),
					qs.getResource("procedure"), qs.getResource("property"),
					qs.getResource("phenomenon"), qs.getResource("ob"),
					qs.getResource("result"), qs.getResource("samplingTime"),
					qs.get("time"));

		}

	}// end function LLDReduction(final String phenomena)

	public static ResultSet getTruthObservation() throws FileNotFoundException {
		/****
		 * The old version of factorization code was creating three molecules; observation, measurement and time molecule. But in new
		 * version we are no more creating time molecule. Therefore, we do not need to sort observations on time value. This function is
		 * modified to remove the retrieval based on time order.
		 ***/
		// final long startTime = System.currentTimeMillis();
		final String queryString = "prefix om-owl: <http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#> "
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "prefix sens-obs: <http://knoesis.wright.edu/ssw/> "
				+ "prefix owl-time: <http://www.w3.org/2006/time#> "
				+ "prefix owl: <http://www.w3.org/2002/07/owl#> "
				+ "prefix xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "prefix weather: <http://knoesis.wright.edu/ssw/ont/weather.owl#> "
				+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "prefix lld: <http://linkeddata.com/ontology#> "
				+ " SELECT ?ob ?v ?u ?property ?procedure ?result ?samplingTime ?time ?phenomenon "
				+ " WHERE { ?ob a ?phenomenon ; "
				+ " om-owl:procedure ?procedure ; "
				+ " om-owl:observedProperty ?property ; "
				+ " om-owl:result ?result ; "
				+ " om-owl:samplingTime ?samplingTime . "
				+ "	?samplingTime owl-time:inXSDDateTime ?time . "
				+ " ?result om-owl:booleanValue ?v . " + "} ";

		final Query query = QueryFactory.create(queryString);
		final QueryExecution qexec = QueryExecutionFactory.create(query,
				Factorizer.originalModel);
		final ResultSet result = qexec.execSelect();
		return result;
	}

	// ////////////////////////////////////////////////////////////////////

	public static void createMolecules(final Boolean value,
			final Resource sensor,
			final Resource property,
			final Resource phenomenon,
			final Resource observation,
			final Resource result,
			final Resource samplingTime,
			final RDFNode time) {

		Resource mURI = Factorizer.mapTruth.get(value);

		if (mURI != null) {
			final observationClass obs = new observationClass(sensor, property,
					phenomenon, mURI);
			final Resource obsURI = Factorizer.mapObservation
					.get(obs);
			if (obsURI != null) {

				Factorizer.reducedModel
						.add(observation,
								Factorizer.reducedModel
										.createProperty("http://linkeddata.com/ontology#observationOf"),
								obsURI);

			} // / end of if (obsURI != null)
			else {
				// System.out.println("observation Molecule NOT found.....");///executes
				createObservationMolecule(obs, observation, result,
						samplingTime, time);
			}
		} // / end of if (mURI != null)
		else {
			mURI = createTruthMolecule(value, result);
			final observationClass obs = new observationClass(sensor, property,
					phenomenon, mURI);
			createObservationMolecule(obs, observation, result, samplingTime,
					time);
		}

	}

	// //////////////////////////////////////////////////////////////////

	public static Resource createTruthMolecule(final Boolean value,
			final Resource result) {
		final Resource new_mURI = Factorizer.reducedModel
				.createResource("http://linkeddata.com/ontology#" + value);

		Factorizer.mapTruth.put(value, new_mURI);
		Factorizer.reducedModel
				.add(new_mURI,
						Factorizer.reducedModel
								.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
						Factorizer.reducedModel
								.createResource("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#TruthData"));
		Factorizer.reducedModel
				.add(new_mURI,
						Factorizer.reducedModel
								.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#booleanValue"),
						Factorizer.reducedModel
								.createTypedLiteral(value));

		return new_mURI;
	}

	// /////////////////////////////////////////////////////////////////////////////////
	public static void createObservationMolecule(final observationClass obs,
			final Resource observation,
			final Resource result,
			final Resource samplingTime,
			final RDFNode time) {
		final Resource new_obsURI = Factorizer.reducedModel
				.createResource("http://linkeddata.com/ontology#"
						+ observation.getLocalName());

		Factorizer.mapObservation.put(obs, new_obsURI);
		Factorizer.reducedModel
				.add(new_obsURI,
						Factorizer.reducedModel
								.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#result"),
						obs.getmURI());
		Factorizer.reducedModel
				.add(new_obsURI,
						Factorizer.reducedModel
								.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
						obs.getPhenomenon());
		Factorizer.reducedModel
				.add(new_obsURI,
						Factorizer.reducedModel
								.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#observedProperty"),
						obs.getProperty());
		Factorizer.reducedModel
				.add(new_obsURI,
						Factorizer.reducedModel
								.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#procedure"),
						obs.getSensor());
		Factorizer.reducedModel
				.add(obs.getSensor(),
						Factorizer.reducedModel
								.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#generatedObservation"),
						new_obsURI);
		Factorizer.reducedModel
				.add(observation,
						Factorizer.reducedModel
								.createProperty("http://linkeddata.com/ontology#observationOf"),
						new_obsURI);

	}
}
