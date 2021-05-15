## Requisites

The only requisites to using this software is having valid AWS credentials, Java 11 installed, and Maven v3.6.3 installed.

## Building

Prior to deploying and running the code in this repository on an AWS instance, the source code MUST be compiled on the local machine using the following command

    sh ./build.sh

## Removal

To remove the compiled code, simply enter the root and run

    sh ./clean.sh