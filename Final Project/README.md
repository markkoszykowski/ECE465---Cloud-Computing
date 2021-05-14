# Distributed Fast Fourier Transform for Image Compression

The Fast Fourier Transform is a series of algorithm developed to efficiently perform Discrete Fourier Transforms (DFT). Various algorithms exist, common one's being Cooley-Tukey Radix-2, Cooley-Tukey Radix-4, Bluestein's algorithm, etc.. The FFT has various uses in signal processing, and in this case, can be applied to image compression. Image's essentially act as 2D signals, and can therefore be transformed using the FFT. Our system uses the Cooley-Tukey Radix-2 Iterative algorithm, with time complexity O(nlogn), an improvement from the DFT with time complexity O(n^2). Images can be divided up into their three color channels, and then operated on independently using the FFT. Working with a single channel, the system performs FFTs row-wise, and then column-wise, treating each row or column as its own 1D signal. A threshold is then applied to the fourier coefficients, zeroing out all coefficients below the threshold. The image can then be reconstructed with the Inverse Fast Fourier Transform (IFFT), whcih involves applying the FFT to conjugated fourier coefficients and then scaling. Once all channels have been thresholded, the image is reconstructed, containing a fraction of the information initially present.

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
