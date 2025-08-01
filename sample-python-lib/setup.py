#!/usr/bin/env python3
"""
Setup script for Sample Python Library
"""

from setuptools import setup, find_packages
import os

# Read the README file
def read_readme():
    with open("README.md", "r", encoding="utf-8") as fh:
        return fh.read()

# Read requirements
def read_requirements():
    with open("requirements.txt", "r", encoding="utf-8") as fh:
        return [line.strip() for line in fh if line.strip() and not line.startswith("#")]

# Get version from version file
def get_version():
    version_file = os.path.join(os.path.dirname(__file__), "version.txt")
    if os.path.exists(version_file):
        with open(version_file, "r", encoding="utf-8") as fh:
            return fh.read().strip()
    return "1.0.0"

setup(
    name="sample-python-lib",
    version=get_version(),
    author="Sample Team",
    author_email="team@company.com",
    description="A sample Python library demonstrating Jenkins CI/CD pipeline usage",
    long_description=read_readme(),
    long_description_content_type="text/markdown",
    url="https://github.com/company/sample-python-lib",
    packages=find_packages(),
    classifiers=[
        "Development Status :: 4 - Beta",
        "Intended Audience :: Developers",
        "License :: OSI Approved :: MIT License",
        "Operating System :: OS Independent",
        "Programming Language :: Python :: 3",
        "Programming Language :: Python :: 3.8",
        "Programming Language :: Python :: 3.9",
        "Programming Language :: Python :: 3.10",
        "Programming Language :: Python :: 3.11",
        "Topic :: Software Development :: Libraries :: Python Modules",
    ],
    python_requires=">=3.8",
    install_requires=read_requirements(),
    extras_require={
        "dev": [
            "pytest>=7.0.0",
            "pytest-cov>=4.0.0",
            "ruff>=0.1.0",
            "mypy>=1.0.0",
            "mutmut>=3.0.0",
            "black>=23.0.0",
            "isort>=5.0.0",
        ],
    },
    entry_points={
        "console_scripts": [
            "sample-lib=sample_python_lib.cli:main",
        ],
    },
    include_package_data=True,
    zip_safe=False,
) 