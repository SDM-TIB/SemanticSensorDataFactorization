# SemanticSensorDataFactorization

The project generates factorized representations of an RDF graph described using the Semantic Sensor Network Ontology.

# 1. Factorization

## Create Maven Package

```
$mvn package
```

## Run Factorization

```
$java -jar target/FactorizationSSN-0.0.1-SNAPSHOT-jar-with-dependencies.jar $path_to_original_RDF_data $path_to_factorized_RDF_data
```
### Parameters
$path_to_original_RDF_data - Path to the original RDF graph to be factorized; an RDF graph described using the Semantic Sensor Network Ontology.

$path_to_factorized_RDF_data - Path to the folder to save the factorized RDF data. 

### Running Factorization using Example

Example json file and original RDF data are provided in the *database* folder. Use the command below to run the example.

```
$cd SemanticSensorDataFactorization
$java -jar target/FactorizationSSN-0.0.1-SNAPSHOT-jar-with-dependencies.jar ./dataset/original/ ./dataset/factorized/
