
echo "Script requires your maven project to be compiled"


DATA_INDEX_VERSION=0.4.0
wget http://repo2.maven.org/maven2/org/kie/kogito/data-index-service/${DATA_INDEX_VERSION}/data-index-service-${DATA_INDEX_VERSION}-runner.jar
java -jar  -Dkogito.protobuf.folder=`pwd`/target/classes/persistence data-index-service-${DATA_INDEX_VERSION}-runner.jar

