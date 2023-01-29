# Forbes Take-Home Test

## 1 - Code Review

## 2 - Code Challenge

All code challenge code is within the `/2-code-challenge` folder.

### Running the code

**Note: To run this, the Java JDK 17 must be installed and the `java` command must be accessible from the terminal.**

Steps to run the application:

```bash
# Clone the repo
git clone https://github.com/takanuva15/forbes-takehometest.git

# cd to the code-challenge root within a terminal
cd C:\..\..\forbes-takehometest\2-code-challenge

# Run gradle build to build the jar
./gradlew build

# Run the built jar file. It will start a web server on port 8080
java -jar build\libs\code-challenge-0.0.1-SNAPSHOT.jar
```

Sample curl commands:
```bash
# add dictionary words (Note: words with digits or punctuation besides ' or - will be ignored)
curl -i http://localhost:8080/dictionary -H "Content-Type: application/json" -d @sample_dict_add.json
curl -i http://localhost:8080/dictionary -H "Content-Type: application/json" -d "{\"dictionary\": {\"add\": [\"cat\", \"bat\"]}}"

# delete dictionary words
curl -i -X "DELETE" http://localhost:8080/dictionary -H "Content-Type: application/json" -d @sample_dict_remove.json
curl -i -X "DELETE" http://localhost:8080/dictionary -H "Content-Type: application/json" -d "{\"dictionary\": {\"remove\": [\"believes\"]}}"

# get dictionary words
curl -i http://localhost:8080/dictionary

# get closest matches for story
curl -i http://localhost:8080/story -H "Content-Type: application/json" -d @sample_story.json
curl -i http://localhost:8080/story -H "Content-Type: application/json" -d "{\"story\": \"mat\"}"

```

### Development stack used

- Java 17
- Spring Boot 3.0.0
- Gradle 7.4
- IntelliJ IDEA 2022.3.1 (Community Edition)

### How to deploy this application

To deploy this application, I would first package it and get it running on a Docker container. Afterwards, I would 
deploy it on an existing server that has the docker engine installed, or on a cloud provider that supports Docker.
This would be sufficient to get a single instance running for testing or on a DEV environment for others to connect 
their own applications to.

In order to add resiliency though, we would need to deploy multiple instances. This leads to a problem because, with
the current implementation, each web server stores its own instance of the dictionary used for autocorrection. Thus,
we can't simply run multiple instances with a load-balancer on top of them without doing some refactoring, since one 
web server's dictionary could have words not present in another's.

One option is to stop storing the dictionary and Trie structure locally on the web server. Instead, we can leverage a 
SQL db table for storing the overall word lists and use a NoSQL db (eg a Graph database) for storing the Trie structure.
Then, when we want to query the Trie for an autocorrect match, our web server would simply trigger a query to run in our
DB. This would probably be the optimal solution for availability and minimal custom configuration on the server. 

Another possible option could be to configure the servers to serve either a "read-only" or "write-and-read" role. In
this case, we will have to start up one server with a "write-and-read" role. This server is the only one permitted to
add new words to the dictionary. Again, we would need to store these words in a proper DB so that we don't lose the data
on a server restart. This server would also build the Trie used for searching for close matches. After that, we can
start up a bunch of "read-only" servers that would receive the word-list and Trie from the "write" server. Anytime our
application needs to query for autocorrect, we would send those requests to a load-balancer that communicates with a 
randomly chosen "read-only" instance that would perform the appropriate query.

However, when someone wanted to update the dictionary, they would only be allowed to use a separate load-balancer URL
that points to the "write-and-read" instance. When the "write-and-read" instance makes an update to the dictionary and
the resulting Trie, it would post an update to a notification channel that all the other "read-only" instances are
subscribed to. The "read-only" instances would listen to the notification and subsequently update their local copies of
the dictionary and Trie based on the update received. (Note that the "write-and-read" instance should periodically save
a copy of its Trie into a DB such as MongoDB so that when new "read" replicas are launched, they can pull the Trie from
the DB rather than needing to reconstruct it. We can also deploy multiple "write-and-read" instances that can listen
to notifications from the other write-and-read instances to update their own DB).

The 2nd solution may require additional checks to ensure that the instances are synced properly and contain the same 
data. (A lot of this is built into a native NoSQL db solution such as a Graph DB)

> ### Side Note on Caching
> Another thing to note is that some typos are very common (eg "teh" -> "the"), so it is not worth querying the Trie for
> these. We can set up a caching layer in front of the DB (eg implemented as part of `TrieDao`), so that these queries get
> an immediate response. (A potential implementation could be to use a distributed cache like Redis, either 
> on-prem or via a cloud provider)


Regardless of the solution picked, it would be definitely be beneficial to set up read replicas for this solution since
reads will be done a lot more than writes (new words would not be added to the dictionary often). 

In addition, with the extra configuration required for load-balancing, caching, and running multiple container 
instances, using a container orchestration engine such as Kubernetes to deploy our application would provide an 
optimal way to configure repeatable deployments defined within files that can be committed to version control.



