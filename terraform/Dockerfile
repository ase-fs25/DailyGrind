FROM hashicorp/terraform:latest

# Install Python, pip, and virtualenv
RUN apk add --no-cache bash python3 py3-pip py3-virtualenv nodejs npm jq aws-cli

# Create a virtual environment
RUN python3 -m venv /venv

# Upgrade pip in the virtual environment
RUN /venv/bin/pip install --upgrade pip

# Install terraform-local within the virtual environment
RUN /venv/bin/pip install terraform-local

# Set the PATH to include the virtual environment's bin directory
ENV PATH="/venv/bin:$PATH"
