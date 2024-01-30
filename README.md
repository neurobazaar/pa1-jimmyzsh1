## CSC435 Programming Assignment 1 (Winter 2024)
**Jarvis College of Computing and Digital Media - DePaul University**

**Student**: Shihua Zhang, jimmyzsh@gmail.com

**Solution programming language**: Java

### Building the Programs
### Prerequisites

Java JDK 11 or later
Maven (for dependency management and building)


First install maven (if not yet installed).
```
sudo apt install openjdk-17-jdk maven

```

### Setup

download the 5 datasets (Dataset1, Dataset2, Dataset3, Dataset4, Dataset5)  from the following link:

https://depauledu-my.sharepoint.com/:f:/g/personal/aorhean_depaul_edu/EgmxmSiWjpVMi8r6QHovyYIB-XWjqOmQwuINCd9N_Ppnug?e=TLBF4V

and unzip them.


```

### Java solution
#### How to build/compile

To build the Java solution use the following commands:
```
cd app-java
app-java is the main directory we are going to work on, because the pom.xml file is here. So we also need to copy the dataset files here.

write the 3 programs CleanDataset, CountWords, and SortWords. 
The program requires two directories as input and output, make sure to write the codes in accordance to it.

mvn compile
mvn package

After writing, use mvn compile and mvn package to check whether they compile correctly.
```
#### How to run application

To run the Java clean dataset program (after you build the project) use the following command:
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.CleanDataset <input directory> <output directory>
```

To run the Java word count program (after you build the project) use the following command:
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.CountWords <input directory> <output directory>
```

To run the Java sort_words program (after you build the project) use the following command:
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.SortWords <input directory> <output directory>
```
