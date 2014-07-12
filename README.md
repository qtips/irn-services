irn-services
============

Getting started with development

Requirements:
Git:
Install git and run 'git clone https://github.com/qtips/irn-services.git'
For Windows install git through http://msysgit.github.io/, open a folder where you wish to check out the code, right-click and select Git Bash and then run the command above

Java 7 and JAVA_HOME

Recommendations:
Intellij IDE: http://www.jetbrains.com/idea/download/
Community edition is free.

Building the code with Gradle
1) In the folder where you checked out the code, run the "gradlew" script (.bat for windows and .sh for MAC/Linux). Now Gradle is ready
2) Run "gradlew test" to compile code and run the tests. 

Starting development with IntelliJ
1) Start IntelliJ
2) Install IntelliJ Scala plugin
3) Import the project (File > Import Project) and select the build.gradle file from the root source directory
4) Project should now be loaded in IntellJ. 
