# Forbes Take-Home Test

This repo can be opened in IntelliJ directly from the root. I have included the *.iml files so the modules should 
hopefully set themselves up automatically. If that doesn't happen for the `#2 Code Challenge`, then you can try 
manually going into IntelliJ's project structure and importing the #2 module directly from the build.gradle file. I
have also added Gradle run configs that should ideally work out-of-the-box assuming you have Java 17 set up in IntelliJ.

(In either case, I have provided instructions for running the server without using IntelliJ at all as specified below)

## 1 - Code Review

All code review comments are present in `/1-code-review/main.js`

## 2 - Code Challenge

All code challenge code is within the `/2-code-challenge` folder.

### Running the code

**Note: To run this, the Java JDK 17 must be installed and the `java` command must be accessible from the terminal.**

**The `java -version` command must return a version of Java 17**

Steps to run the application:

```bash
# Clone the repo
git clone https://github.com/takanuva15/forbes-takehometest.git

# cd to the code-challenge root within a terminal
cd C:\..\..\forbes-takehometest\2-code-challenge

# Run gradle build to build the jar
gradlew build 
# ./gradlew build for Linux

# Run the built jar file. It will start a web server on port 8080
java -jar build\libs\code-challenge-0.0.1-SNAPSHOT.jar
```

Sample curl commands (must be run in a separate terminal from the jar file, but within the same directory as the 
code-challenge. I ran these commands in standard cmd, but you may need to format these a little if using powershell):
```bash
# add dictionary words 
# Note: words with digits or punctuation besides ' or - will be ignored. I coded this logic on the idea that it 
# wouldn't make sense to autocorrect something like "200" to "20". This could be re-implemented easily by changing
# the validation to allow digits.
# In addition, for words that don't meet the digit/punctuation validation checks, currently I silently ignore them and 
# proceed with the next word. I did this to match the ApiSpec given, where we should process all valid words and only
# return response 200 if the entry is a duplicate. This could be better-handled in the future by returning a 400 
# immediately if any of the words were invalid, and then returning response 200 only if all words are valid and some 
# are duplicates. 
curl -i http://localhost:8080/dictionary -H "Content-Type: application/json" -d @sample_dict_add.json
curl -i http://localhost:8080/dictionary -H "Content-Type: application/json" -d "{\"dictionary\": {\"add\": [\"cat\", \"bat\"]}}"

# delete dictionary words
curl -i -X "DELETE" http://localhost:8080/dictionary -H "Content-Type: application/json" -d @sample_dict_remove.json
curl -i -X "DELETE" http://localhost:8080/dictionary -H "Content-Type: application/json" -d "{\"dictionary\": {\"remove\": [\"believes\"]}}"

# get dictionary words
# Note: At first, I was storing the dictionary by order of insertion, but it looks like the API Spec was intending me
# to return words in sorted order so that's what I'm doing. If we wanted to return words in insertion-order, we could 
# easily modify `WordStorageDao` to use a LinkedHashMap with a sequence number to store words in insertion order 
# and return the closest match based on when it was inserted.
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

To deploy this application, I would first package it and get it running on a Docker container to avoid concerns about
different server configuration, where Java is installed, directory permissions, etc. 

Afterwards, I would deploy it on an existing server that has the docker engine installed, or on a cloud provider (eg
AWS, GCP, etc.) that supports Docker.
This would be sufficient to get a single instance running for testing or on a DEV environment for others to connect 
their own applications to.

In order to add resiliency though, we would need to deploy multiple instances. This leads to a problem because, with
the current implementation, each web server stores its own instance of the dictionary used for autocorrection. Thus,
we can't simply run multiple instances with a load-balancer on top of them without doing some refactoring, since one 
web server's dictionary could have words not present in another's.

One option is to stop storing the dictionary and Trie structure locally on the web server. Instead, we can leverage a 
 db table for storing the overall word list and use a NoSQL db (eg a Graph database) for storing the Trie structure.
Then, when we want to query the Trie for an autocorrect match, our web server would simply trigger a query to run in our
DB. This would probably be the optimal solution for resiliency and minimal custom configuration on the server. 

Another possible option could be to configure the servers to serve either a "read-only" or "write-and-read" role. In
this case, we will have to start up one server with a "write-and-read" role. This server is the only one permitted to
add new words to the dictionary. Again, we would need to store the word list in a proper DB so that we don't lose the
data on a server restart. This server would also build the Trie used for searching for close matches. After that, we can
start up a bunch of "read-only" servers that would receive the word-list and Trie from the "write" server. Anytime our
application needs to query for autocorrect, we would send those requests to a load-balancer that communicates with a 
randomly chosen "read-only" instance that would perform the appropriate query.

However, when someone wanted to update the dictionary, they would only be allowed to use a separate load-balancer URL
that points to the "write-and-read" instance. When the "write-and-read" instance makes an update to the dictionary (and
the resulting Trie), it would post an update to a notification channel that all the other "read-only" instances are
subscribed to. The "read-only" instances would listen to the notification and subsequently update their local copies of
the dictionary and Trie based on the update received. (Note that the "write-and-read" instance should periodically save
a copy of its Trie into a DB such as MongoDB so that when new "read" replicas are launched, they can pull the Trie from
the DB rather than needing to reconstruct it. We can also deploy multiple "write-and-read" instances that can listen
to notifications from the other write-and-read instances to update their own DB. This helps with resiliency for the
"write-and-read" instances).

The 2nd solution may require additional checks to ensure that the instances are synced properly and contain the same 
data. (A lot of this is built into a native NoSQL db solution such as a Graph DB). In addition, the current solution 
has not been thoroughly tested for potential concurrency issues with multiple users updating the dictionary at the
same time, so this would need to be evaluated as well through multi-threaded performance testing. 

> ### Side Note on Caching
> Another thing to note is that some typos are very common (eg "teh" -> "the"), so it is not worth querying the Trie for
> these. We can set up a caching layer in front of the DB (eg implemented as part of `TrieDao`), so that these queries get
> an immediate response. (A potential implementation could be to use a distributed cache like Redis, either 
> on-prem or via a cloud provider)


Regardless of the solution picked, it would be definitely be beneficial to set up multiple read replicas for this 
solution (either through the microservice's role or a native DB implementation) since reads will be done a lot more 
than writes (new words would not be added to the dictionary often). 

In addition, with the extra configuration required for load-balancing, caching, and running multiple container 
instances, using a container orchestration engine such as Kubernetes to deploy our application would provide an 
optimal way to configure repeatable deployments defined within files that can be committed to version control. This 
also allows us to use Kubernetes' native implementation for zero-downtime deployments such as rolling updates and canary
releases.



