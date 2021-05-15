## AWS Installation

Run

    sh ./aws/create_ec2.sh

to set up the VPC and EC2 instances from the root directory. To install Java and deploy the compiled program to the instances run

    sh ./aws/deploy.sh

With Java installed and the binaries deployed, running the binaries can be done with

    sh ./aws/run.sh

At this point a backend server IP will be provided, take note of this for usage later.

## Termination

Terminating instances once done with usage is highly advised. This can be done by running the following command from the root directory

    sh ./aws/terminate.sh