[![DOI](https://zenodo.org/badge/650538391.svg)](https://zenodo.org/badge/latestdoi/650538391)

# SeFNet: Bridging Tabular Datasets with Semantic Feature Nets

This repository contains code and resources that can be used to reproduce the results presented in the article "SeFNet: Bridging Tabular Datasets with Semantic Feature Nets".

## Reproducting results
### 0. Annotating datasets <br>
The annotation of datasets' features is a tedious process, so the annotations we made manually have been made available in the `annotations` directory. Every annotation file is in .csv format and it consists of two columns: column_name (original feature names) and term_id (SNOMED-CT term ids).

### 1. Calculating similarity between terms <br>
Similarity of terms is callculated using Maven. The necessary dependency information and java configuration are contained in the file pom.xml. Key functionalities used, such as computing semantic similarity between terms, have been implemented in the [slib-sml](https://github.com/sharispe/slib) library.

In order to reproduce our results you have to first get access to [SNOMED-CT ontology](https://www.snomed.org/get-snomed). After downloading the ontology place the folder in the main catalog of the repository. In our research we have used the US version released on March 1, 2023.

When ontology files are present all that is needed is to execute AllTermsSimilarity.java.

### 2. Calculating DOSS matrix <br>
Before the DOSS matrix can be calculated, python and the necessary packages must be installed (`numpy` and `pandas`). We have used python 3.9 and the versions of the packages specified in requirements.txt.
```
pip install -r requirements.txt
```
Now all that is required is to exectute the script:
```
python DOSS.py
```

## Repository structure
```
├── annotations - directory containing datasets annotations
├── calculate-term-similarities
│   ├── src/main/java
│   │   ├── AllTermsSimilarity.java - calculate semantic similarity between all annotated terms (term_similarities.csv)
│   │   ├── Dataset2DatasetSimilarity.java - calculate semantic similarity between terms in two datasets
│   │   ├── SingleTermSimilarity.java - calculate semantic similarity between two terms
│   ├── pom.xml - maven project configuration
├── datasets - directory containing datasets which could be shared
├── DOSS.py - python script which creates DOSS_matrix.csv
├── DOSS_matrix.csv
├── README.md
├── annotations.csv - annotations of all used terms
├── requirements.txy - python necessary packages
└── term_similarities.csv - semantic similarity between all annotated terms calculated in AllTermsSimilarity.java
```

## Citation
```
TBC
```
