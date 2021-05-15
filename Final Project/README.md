# Distributed Fast Fourier Transform for Image Compression

The Fast Fourier Transform is a series of algorithm developed to efficiently perform Discrete Fourier Transforms (DFT). Various algorithms exist, common ones being Cooley-Tukey Radix-2, Cooley-Tukey Radix-4, Bluestein's algorithm, etc.. The FFT has various uses in signal processing, and in this case, can be applied to image compression. Image's essentially act as 2D signals, and can therefore be transformed using the FFT. Our system uses the Cooley-Tukey Radix-2 Iterative algorithm, with time complexity O(nlogn), an improvement from the DFT with time complexity O(n^2). Images can be divided up into their three color channels, and then operated on independently using the FFT. Working with a single channel, the system performs FFTs row-wise, and then column-wise, treating each row or column as its own 1D signal. A threshold is then applied to the Fourier coefficients, zeroing out all coefficients below the threshold. The image can then be reconstructed with the Inverse Fast Fourier Transform (IFFT), which involves applying the FFT to conjugated Fourier coefficients and then scaling. Once all channels have been thresholded, the image is reconstructed, containing a fraction of the information initially present.
This specific production of FFT utilizes AWS's cloud services to implement a MapReduce architecture in which users can upload and download images and their compressed versions, respectively, while choosing a compression rate.
At this current time, the server is only capable of serving one client at a time.

## Example

|Original |  Compressed (with 0.05 compression rate) |
|--------|------|
|![](front/original.jpg)  |  ![](front/compressed.jpg)|
| 32,841 bytes | 19,416 bytes |

[Presentation Link](https://www.youtube.com/watch?v=CD4JQmxGARc&ab_channel=OmarThenmalai)

## Solution Architecture Diagram

![](front/Cloud%20Architecture.png)

## Goals for future implementations:

 - More efficient method of dealing with images that are non radix 2 - currently using zero-padding
 - A dynamically adjusting algorithm that takes into account the number of workers present - currently uses a fixed algorithm for divide and conquer equally amongst nodes, however in the case in which the number of workers does not exceed the number of channels, it is more efficient for each node to perform all necessary computation at once on their respective channel
 - A queuing system to allow multiple clients to request compression services - currently the website can serve multiple clients visually but is incapable of handling multiple compression requests concurrently
 - An improved frontend - either using AWS S3 buckets to allow for static URLs or AWS Amplify to incorporate JavaScript React

## Usage

Once the binaries have been deployed to the EC2 instances and running, one can access the capabilities of our service by going to the following web address

    <ip_provided_when_running_binaries>:4567

Here, users can upload images which will be automatically downloaded in a compressed file in the same image format as provided.
Users simply must specify a compression rate - this rate indicates the percentage between {0.0, 1.0} of Fourier coefficients which are maintained in the image.

## Authors

- Mark Koszykowski
- Omar Thenmalai

## References

- [FFT Based Compression approach for Medical Images](https://www.ripublication.com/ijaer18/ijaerv13n6_54.pdf)
- [Image Compression Using Fourier Techniques](https://www.maths.usyd.edu.au/u/olver/teaching/Computation/ExampleProject.pdf)
