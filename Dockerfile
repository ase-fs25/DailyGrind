# Description: Dockerfile for building a container image with Terraform and terraform-local installed
# Currently not used since there are issues with terraform-local in the container


#FROM hashicorp/terraform:latest
#
## Install Python, pip, and virtualenv
#RUN apk add --no-cache python3 py3-pip py3-virtualenv
#
## Create a virtual environment
#RUN python3 -m venv /venv
#
## Upgrade pip in the virtual environment
#RUN /venv/bin/pip install --upgrade pip
#
## Install terraform-local within the virtual environment
#RUN /venv/bin/pip install terraform-local
#
## Set the PATH to include the virtual environment's bin directory
#ENV PATH="/venv/bin:$PATH"