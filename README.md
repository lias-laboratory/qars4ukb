# QaRS4UKB

Uncertain Knowledge Bases (UKB) contain facts captured as RDF triples (subject, predicate, object), where each triple is associated to an explicit degree of certainty. When querying UKB, users expect to obtain high quality results i.e, results that have a certainty degree greater than a given threshold α. Yet, they may face the empty answer problem i.e, they obtain no result or results that do not satisfy the degree of certainty α. Instead of solely returning an empty set as the answer of a query, this project proposes algorithms to help the user understand the reasons of this failure with a set of αMinimal Failing Subqueries (αMFSs), and suggest alternative relaxed queries, called αMaXimal Succeeding Subqueries (αXSSs).

More precisely, this project implements the following algorithms. 

* αLBA: computes αMFSs and αXSSs of a failing RDF query for a given threshold α.
* NLBA, Bottom-Up, Top-Down and Hybrid: computes αMFSs and αXSSs of a failing RDF query for several thresholds.

## Software requirements

* Java version >= 8.
* An integrated development environment (i.e. Eclipse: https://eclipse.org/downloads/).
* All operating systems that support at least the Java 8 version.
* Maven.
* WatDiv benchmark: http://dsg.uwaterloo.ca/watdiv/

## Compilation 

* Compile the project and deploy the artifcats to the local Maven repository.

```console
$ mvn clean install
```

## Usage

* Create a sample Maven project

* Add QaRS Maven dependency

```xml
<groupId>fr.ensma.lias</groupId>
<artifactId>qars4ukb</artifactId>
<version>0.1-SNAPSHOT</version>
```

### Step 1: Generate dataset from Watdiv benchmark

* Go to the _bin/Relase_ directory of WatDiv: _cd bin/Release_.

* To generate a tiny dataset of 100K triples, execute the following command. The result will be stored into the _watdiv100K.nt_ file.

```console
$ ./watdiv -d ../../model/wsdbm-data-model.txt 1 > ~/watdiv100K.nt 
```

### Step 2: Generate N-Quads

* Use the _script/trustrandomize.sh_ script.

```console
$ ./trustrandomize.sh ~/watdiv100K.nt > ~/watdiv100K.qt
```

### Step 3: Using the NLBA, BottomUp, TopDown and Hybrid Algorithms

* Create a snippet class (i.e. `QaRS4UKMSample`) with a Java main method and fill it with the following code.

* Initialize a JenaTDB instance by using the memory version.

```java
public class QaRS4UKMSample {
    public static void main(String[] args) {	
        Location locationMemory = Location.mem("watdivtdbmemory");
        DatasetGraph currentGraph = TDBFactory.createDatasetGraph(locationMemory);
        Dataset dataset = TDBFactory.createDataset(locationMemory);
        dataset.begin(ReadWrite.READ);
        TDB.getContext().setTrue(TDB.symUnionDefaultGraph);
```

* Load the N-Quads from the previous generated file.

```java
        FileManager fm = FileManager.get();
        InputStream in = fm.open(".../test_jena_quad.nq");
        TDBLoader.load(TDBInternal.getBaseDatasetGraphTDB(currentGraph), in, true);
```

* Define the SPARQL query.

```
        QueryFactory factory = new JenaTDBGraphQueryOptFactory();
        Query createQuery = factory.createQuery(
                "SELECT * WHERE { ?p <http://db.uwaterloo.ca/~galuc/wsdbm/friendOf> ?f . ?f <http://db.uwaterloo.ca/~galuc/wsdbm/likes> ?p . ?p <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://db.uwaterloo.ca/~galuc/wsdbm/ProductCategory> }");
```

* Choose an algorithm.

```java
        IAlgo algo = new AlgoNLBA(((JenaTDBGraphQueryOptFactory) factory).createSession(dataset));
        // IAlgo algo = new AlgoBottomUp(((JenaTDBGraphQueryOptFactory) factory).createSession(dataset));
        // IAlgo algo = new AlgoHybrid(((JenaTDBGraphQueryOptFactory) factory).createSession(dataset));
        // IAlgo algo = new AlgoTopDown(((JenaTDBGraphQueryOptFactory) factory).createSession(dataset));
```

* Execute the algorithm.

```java
        algo.computesAlphaMFSsAndXSSs(createQuery, Arrays.asList(0.0, 0.2, 0.4, 0.6, 0.8, 1.0));
```

### Step 4: Display the statistics and the final results

* Statistics on the executed algorithm.

```java
        System.out.println(
                "Time = " + algo.getComputingTime() + 
                ", NbQueriesExecuted: " + algo.getNbExecutedQuery() + 
                ", NbCacheHits: " + algo.getNbCacheHits());
```

* Statistics on MFS and XSS.

```java
        System.out.println(
                "NbMFS: " + createQuery.getAllMFS().size() + 
                ", NbXSS: " + createQuery.getAllXSS().size());
```

* Display results on 0.2 MFS and 0.2 XSS.

```java
        System.out.println(computesAlphaMFSsAndXSSs.getAlphaMFSs(0.2).size());
        System.out.println(computesAlphaMFSsAndXSSs.getAlphaXSSs(0.2).size());
    }
}
```

## Experimentations

Our experiments were conducted on a Ubuntu Server 16.04 LTS system with Intel XEON CPU E5-2630 v3 @2.4Ghz CPU and 16GB RAM. For our experiments, we use arbitrarily the @min@ aggregate function. All times presented are the average of five consecutive runs of the algorithms. To prevent a cold start effect, a preliminary run is performed but not included in the results.

### Queries

```
# Query Q1-3TP
SELECT * WHERE { ?p <http://db.uwaterloo.ca/~galuc/wsdbm/friendOf> ?f . ?f <http://db.uwaterloo.ca/~galuc/wsdbm/likes> ?p . ?p <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://db.uwaterloo.ca/~galuc/wsdbm/ProductCategory> }

# Query Q2-6TP-CHAIN
SELECT * WHERE { <http://db.uwaterloo.ca/~galuc/wsdbm/User666524> <http://db.uwaterloo.ca/~galuc/wsdbm/likes> ?v0 . ?v0 <http://db.uwaterloo.ca/~galuc/wsdbm/hasGenre> ?v1 . ?v1 <http://ogp.me/ns#tag> <http://db.uwaterloo.ca/~galuc/wsdbm/Topic129> . ?v0 <http://db.uwaterloo.ca/~galuc/wsdbm/friendOf> ?v2 . ?v2 <http://purl.org/dc/terms/Location> ?v3 . ?v3 <http://www.geonames.org/ontology#parentCountry> <http://db.uwaterloo.ca/~galuc/wsdbm/Country17> }

# Query Q3-7TP
SELECT * WHERE { ?v0 <http://db.uwaterloo.ca/~galuc/wsdbm/follows> ?v1 . ?v1 <http://db.uwaterloo.ca/~galuc/wsdbm/follows> ?v0 . ?v1 <http://db.uwaterloo.ca/~galuc/wsdbm/subscribes> ?v2 . ?v0 <http://db.uwaterloo.ca/~galuc/wsdbm/subscribes> ?v2 . ?v1 <http://db.uwaterloo.ca/~galuc/wsdbm/likes> <http://db.uwaterloo.ca/~galuc/wsdbm/Product16770> . ?v0 <http://schema.org/nationality> <http://db.uwaterloo.ca/~galuc/wsdbm/Country20> . ?v0 <http://db.uwaterloo.ca/~galuc/wsdbm/makesPurchase> ?v3 }

# Query Q4-STAR-8TP
SELECT * WHERE { ?v0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://db.uwaterloo.ca/~galuc/wsdbm/User> . ?v0 <http://xmlns.com/foaf/familyName> 'Smith' . ?v0 <http://db.uwaterloo.ca/~galuc/wsdbm/subscribes> <http://db.uwaterloo.ca/~galuc/wsdbm/Website362909> . ?v0 <http://db.uwaterloo.ca/~galuc/wsdbm/follows> ?v1 . ?v0 <http://db.uwaterloo.ca/~galuc/wsdbm/friendOf> ?v2 . ?v0 <http://db.uwaterloo.ca/~galuc/wsdbm/likes> ?v3 . ?v0 <http://db.uwaterloo.ca/~galuc/wsdbm/userId> ?v4 . ?v0 <http://db.uwaterloo.ca/~galuc/wsdbm/makesPurchase> ?v5 . ?v0 <http://purl.org/dc/terms/Location> ?v6 . ?v0 <http://schema.org/nationality> ?v7 . ?v0 <http://db.uwaterloo.ca/~galuc/wsdbm/userId> ?v8 }  

# Query Q5 10-TP
SELECT * WHERE { ?p <http://db.uwaterloo.ca/~galuc/wsdbm/likes> ?x . ?x <http://db.uwaterloo.ca/~galuc/wsdbm/likes> ?p . ?p <http://db.uwaterloo.ca/~galuc/wsdbm/hasGenre> <http://db.uwaterloo.ca/~galuc/wsdbm/SubGenre92> . ?x <http://db.uwaterloo.ca/~galuc/wsdbm/subscribe> ?w1 . ?w1 <http://schema.org/language> <http://db.uwaterloo.ca/~galuc/wsdbm/Language21> . <http://db.uwaterloo.ca/~galuc/wsdbm/Website121> <http://db.uwaterloo.ca/~galuc/wsdbm/hits> ?h . ?x <http://xmlns.com/foaf/homepage> <http://db.uwaterloo.ca/~galuc/wsdbm/Website120> . ?x <http://xmlns.com/foaf/familyName> 'Smith' . ?x <http://db.uwaterloo.ca/~galuc/wsdbm/friendOf> ?x2 . ?x2 <http://schema.org/email> 'xxx@xxx.com' } 

# Query Q6 12-TP
SELECT * WHERE { ?v0 <http://schema.org/eligibleRegion> <http://db.uwaterloo.ca/~galuc/wsdbm/Country05> . ?v0 <http://purl.org/goodrelations/includes> ?v1 . <http://db.uwaterloo.ca/~galuc/wsdbm/Retailer1257> <http://purl.org/goodrelations/offers> ?v0 . ?v0 <http://purl.org/goodrelations/price> '90' . ?v0 <http://purl.org/goodrelations/serialNumber> ?v4 . ?v0 <http://purl.org/goodrelations/validFrom> ?v5 . ?v0 <http://purl.org/goodrelations/validThrough> ?v6 . ?v0 <http://schema.org/eligibleQuantity> ?v8 . ?v0 <http://schema.org/priceValidUntil> ?v11 . ?v1 <http://ogp.me/ns#tag> ?v7 . ?v1 <http://schema.org/keywords> ?v10 . ?v12 <http://db.uwaterloo.ca/~galuc/wsdbm/purchaseFor> ?v1 }

# Query Q7 15-TP
SELECT * WHERE { ?v0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://db.uwaterloo.ca/~galuc/wsdbm/ProductCategory7> . ?v0 <http://ogp.me/ns#tag> <http://db.uwaterloo.ca/~galuc/wsdbm/Topic245> . ?v0 <http://purl.org/stuff/rev#hasReview> ?v4 . ?v0 <http://schema.org/contentSize> ?v9 . ?v0 <http://schema.org/description> ?v10 . ?v0 <http://schema.org/keywords> ?v11 . ?v12 <http://db.uwaterloo.ca/~galuc/wsdbm/purchaseFor> ?v0 . ?v2 <http://ogp.me/ns#tag> ?v1 . ?v4 <http://purl.org/stuff/rev#rating> ?v5 . ?v4 <http://purl.org/stuff/rev#reviewer> ?v6 . ?v4 <http://purl.org/stuff/rev#text> ?v7 . ?v4 <http://purl.org/stuff/rev#title> ?v8 . ?v6 <http://xmlns.com/foaf/familyName> ?v13 . ?v6 <http://schema.org/birthDate> ?v14 . ?v0 <http://db.uwaterloo.ca/~galuc/wsdbm/gender> ?v15 }
```

## Software licence agreement

Details the license agreement of QaRS4UKB: [LICENCE](LICENCE)

## Historic Contributors

* [Ibrahim DELLAL (core developer)](https://www.lias-lab.fr/members/ibrahimdellal/)
* [Mickael BARON](https://www.lias-lab.fr/members/mickaelbaron/)
* [Brice CHARDIN](https://www.lias-lab.fr/members/bricechardin/)
* [Allel HADJALI](https://www.lias-lab.fr/members/allelhadjali/)
* [Stéphane JEAN](https://www.lias-lab.fr/members/stephanejean/)

## Code analysis

* Lines of Code: 2 807
* Programming Language: Java