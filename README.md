aws-tesseract
=============

##Introduction

The aws-tesseract project is part of the final assignment for IN4392 Cloud computing course. The purpose is to get hands-on experience with a popular cloud provider like [Amazon EC2](http://aws.amazon.com/ec2/) and leverage the concepts discussed in the lectures. The aws-tesseract is a simple webservice for extracting text from images.

## Project Strucutre

* ``awsseract-frontend``: Play! Framework and MasterNode
* ``awsseract-core``: Master and Worker Implementation; includes two runnable daemons: Master Node(without using play!) and Worker Node
* ``awsseract-shared``: Message Protocal files shared by the core and frontend
* ``awssercat-ec2interface``: Amazon EC2 Library Wrapper for manually mointoring and launching EC2 instances

## Group members

* Joseph Hejderup
* Wing Lung Ngai
