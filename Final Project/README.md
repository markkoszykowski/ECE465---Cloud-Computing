# Distributed Fast Fourier Transform for Image Compression

Some notes on FFT and how it works... yadda yadda yadda

## AWS Usage (This will be automated in the backend server eventually)

Once the Git repository has been cloned, simply enter the root and run

    sh ./AWS/create_ec2.sh

to set up the VPC and EC2 instances. To install Java and deploy the compiled program to the instances run

    sh ./AWS/deploy.sh

With Java installed and the binaries deployed, running the binaries can be done with

    sh ./AWS/run.sh

Terminate instances once done with usage. (This will NOT be automated in the backend) This can be done by running

    sh ./AWS/terminate.sh

## Usage

Once the Git repository has been cloned, simply enter the root and run

    sh ./build.sh

At this point, a 'target' folder should have been created. To startup the backend server of this service, enter the 'target' folder and run

    java -cp ece465_finalProject-v2.2-jar-with-dependencies.jar edu.cooper.ece465.App

At this point, a backend server will be running which is accessible through the following domain

    localhost:4567

or

    127.0.0.1:4567

Right now image uploading is available. To download the image once you have uploaded, goto the following domain

    localhost:4567/<Photo_ID_Provided_At_Upload>

or

    127.0.0.1:4567/<Photo_ID_Provided_At_Upload>

## Removal

To remove the compiled code, simply enter the root and run

    sh ./clean.sh


## Authors

- Mark Koszykowski
- Omar Thenmalai

## References

- [FFT Based Compression approach for Medical Images](https://www.ripublication.com/ijaer18/ijaerv13n6_54.pdf)
- [Image Compression Using Fourier Techniques](https://www.maths.usyd.edu.au/u/olver/teaching/Computation/ExampleProject.pdf)