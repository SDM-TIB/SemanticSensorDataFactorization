import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Resource;

public class FloatFactorizer {

	public static void factorize() throws ParseException, IOException {
		// final long startTime = System.currentTimeMillis();
		final ResultSet results = getObservation();
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
							qs.get("time"));
			// System.out.println(qs.get("time"));

			createMolecules(qs.getLiteral("v").getFloat(),
					qs.getResource("?u"), qs.getResource("procedure"),
					qs.getResource("property"), qs.getResource("phenomenon"),
					qs.getResource("ob"));

		}
		// /////////////////////////////
	}// end function LLDReduction(final String phenomena)

	public static ResultSet getObservation() throws FileNotFoundException {
		/****
		 * The old version of factorization code was creating three molecules; observation, measurement and time molecule. But in new
		 * version we are no more creating time molecule. Therefore, we do not need to sort observations on time value. This function is
		 * modified to remove the retrieval based on time order.
		 * Further, the factorization is done incrementally, i.e., without factorizing from scratch the previously factorized data.
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
				+ " ?result om-owl:uom ?u ; " + " om-owl:floatValue ?v . } ";

		final Query query = QueryFactory.create(queryString);
		final QueryExecution qexec = QueryExecutionFactory.create(query,
				Factorizer.originalModel);
		final ResultSet result = qexec.execSelect();
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////

	public static void createMolecules(final Float value,
			final Resource uom,
			final Resource sensor,
			final Resource property,
			final Resource phenomenon,
			final Resource observation) {
		final measurementClass measurement = new measurementClass(value, uom);
		Resource mURI = Factorizer.mapMeasurement
				.get(measurement);
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
				// Factorizer.reducedModel.add(obsURI,Factorizer.reducedModel.createProperty("http://linkeddata.com/ontology#hasObservation"),observation);
			} // / end of if (obsURI != null)
			else {
				createObservationMolecule(obs, observation);
			}
		} // / end of if (mURI != null)
		else {
			mURI = createMeasurmentMolecule(measurement);
			final observationClass obs = new observationClass(sensor, property,
					phenomenon, mURI);
			createObservationMolecule(obs, observation);
		}
	}

	// //////////////////////////////////////////////////////////////////////

	public static Resource createMeasurmentMolecule(
			final measurementClass measurement) {
		final String valueStr[] = String.valueOf(measurement.getValue()).split(
				"\\.");
		final Resource new_mURI = Factorizer.reducedModel
				.createResource("http://linkeddata.com/ontology#"
						+ measurement.getUOM().getLocalName() + valueStr[0]
						+ "DP" + valueStr[1]);
		Factorizer.mapMeasurement.put(measurement, new_mURI);
		Factorizer.reducedModel
				.add(new_mURI,
						Factorizer.reducedModel
								.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
						Factorizer.reducedModel
								.createResource("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#MeasureData"));

		Factorizer.reducedModel
				.add(new_mURI,
						Factorizer.reducedModel
								.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#floatValue"),
						Factorizer.reducedModel
								.createTypedLiteral(measurement.getValue()));

		Factorizer.reducedModel
				.add(new_mURI,
						Factorizer.reducedModel
								.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#uom"),
						measurement.getUOM());
		return new_mURI;
	}

	// ///////////////////////////////////////////////////////////////////////////

	public static void createObservationMolecule(final observationClass obs,
			final Resource observation) {
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
