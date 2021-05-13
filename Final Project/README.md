# Distributed Fast Fourier Transform for Image Compression

Some notes on FFT and how it works... yadda yadda yadda

## Example

|Original |  Compressed (with 0.05 compression rate) |
|--------|------|
|![](front/original.jpg)  |  ![](front/compressed.jpg)|
| 32,841 bytes | 19,416 bytes |

## Requisites

Prior to deploying and running the code in this repository on an AWS instance, the source code must be compiled on the local machine using the following command

    sh ./build.sh

## AWS Usage

Run

    sh ./aws/create_ec2.sh

to set up the VPC and EC2 instances. To install Java and deploy the compiled program to the instances run

    sh ./aws/deploy.sh

With Java installed and the binaries deployed, running the binaries can be done with

    sh ./aws/run.sh

At this point a backend server IP will be provided, take note of this for usage later.
Terminating instances once done with usage. This can be done by running

    sh ./aws/terminate.sh

## Usage

Once the binaries have been deployed to the EC2 instances and running, one can access the capabilities of our service by going to the follow web address

    <ip_provided_when_running_binaries>:4567

Here, users can upload images which will be automatically downloaded in a compressed file in the same image format as provided.
Users simply must specify a compression rate - this rate indicates the percentage between {0.0, 1.0} of Fourier coefficients which are maintained in the image.

## Removal

To remove the compiled code, simply enter the root and run

    sh ./clean.sh


## Authors

- Mark Koszykowski
- Omar Thenmalai

## References

- [FFT Based Compression approach for Medical Images](https://www.ripublication.com/ijaer18/ijaerv13n6_54.pdf)
- [Image Compression Using Fourier Techniques](https://www.maths.usyd.edu.au/u/olver/teaching/Computation/ExampleProject.pdf)