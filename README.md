aws-tesseract
=============

##Introduction

The aws-tesseract project is part of the final assignment for IN4392 Cloud computing course. The purpose is to get hands-on experience with a popular cloud provider like [Amazon EC2](http://aws.amazon.com/ec2/) and leverage the concepts discussed in the lectures. The aws-tesseract is a simple webservice for extracting text from images.


## Build the web interface

### Requirements
* [Play! Framework](http://www.playframework.com/documentation/2.2.x/Installing)
* Firefox to make use of all features (Drag-and-Drop not supported on Chrome)

### Running it

* In the project root folder change to the awsseract folder:  `cd awsseract/`
* Running in developer mode: `play run` and the web app is typically accesible at `localhost:9000`
* Running in production mode: `play start 80`


## Setting up the development environment
There are plugins to easily integrate AWS services with popular IDE's such as Eclipse and IntelliJ

### IntelliJ
* An AWS plugin is available from the JetBrains plugin manager. Full documentation is available [here](https://docs.google.com/document/d/19hE4N2ctSeU9Pdb7CHCucMG3qeqPNwp1gsnb0IAsLJ8/edit?pli=1)
* Play! Application can be set-up to be IntelliJ Projects by simply running this command `play idea` and the project can be imported directly in IntelliJ


### Eclipse
Amazon has launched an official plugin named `AWS Toolkit for Eclipse` its available for download [here](http://aws.amazon.com/eclipse/)


## Group members

* Joseph Hejderup
* Wing Lung Ngai
