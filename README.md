# SemanticSensorDataIncrementalFactorization

The project incrementally generates the factorized representations of an RDF graph described using the Semantic Sensor Network Ontology. If there already exists a factorized dataset, the project uses the Observation and Measurement mappings of the factorized dataset and generates the factorized representations of the input original RDF data without factorizing the previously factorized dataset.

# 1. Factorization

## Create Maven Package

```
$mvn package
```

## Run Factorization

### Running Factorization from scratch

Initially there is no factorized RDF dataset, therefore three argumets are required, i.e., the path to the original RDF data, path to the folder to save the generated factorized representations of the original RDF data, and the path to the folder to save the Observation and Measurement mappings.

```
$java -jar target/FactorizationSSN-0.0.1-SNAPSHOT-jar-with-dependencies.jar $path_to_original_RDF_data $path_to_factorized_RDF_data $path_to_mappings
```

#### Parameters
$path_to_original_RDF_data - Path to the original RDF graph to be factorized; an RDF graph described using the Semantic Sensor Network Ontology.

$path_to_factorized_RDF_data - Path to the folder to save the factorized RDF data.

$path_to_mappings - Path to the folder to save observation and  measurement mappings files.

#### Running Factorization from scratch using Example

Example original RDF data are provided in the *dataset* folder. Use the command below to run the example.

```
$cd SemanticSensorDataFactorization
$mkdir ./dataset/hashmaps
$java -jar target/FactorizationSSN-0.0.1-SNAPSHOT-jar-with-dependencies.jar ./dataset/original/ ./dataset/factorized/ ./dataset/hashmaps
```
 
### Running Factorization with previously factorized RDF data

In case, there exists an already factorized RDF data, therefore, the path to the Observation and Measurement mappings is required along with the path to the original and factorized RDF datasets.
```
$java -jar target/FactorizationSSN-0.0.1-SNAPSHOT-jar-with-dependencies.jar $path_to_original_RDF_data $path_to_factorized_RDF_data $path_to_observation_mappings $path_to_measurement_mappings
```

#### Parameters
$path_to_original_RDF_data - Path to the original RDF graph to be factorized; an RDF graph described using the Semantic Sensor Network Ontology.

$path_to_factorized_RDF_data - Path to the folder to save the factorized RDF data. 

$path_to_observation_mappings - Path to the observation mappings file.

$path_to_measurement_mappings - Path to the measurement mappings file.

#### Running Factorization with example when previously factorized RDF data already exists

Example original RDF data and mappings are provided in the *dataset* folder. Use the command below to run the example.

```
$cd SemanticSensorDataFactorization
$java -jar target/FactorizationSSN-0.0.1-SNAPSHOT-jar-with-dependencies.jar ./dataset/original/ ./dataset/factorized/ ./dataset/hashmaps/obshashmap.json ./dataset/hashmaps/meashashmap.json
```
