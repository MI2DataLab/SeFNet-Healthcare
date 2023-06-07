import pandas as pd
import numpy as np
import os
from copy import deepcopy


# Paths
DIR_ANNOTATIONS = "annotations/"

# Similarities bewteen terms
sims = pd.read_csv("all_similarities.csv", index_col=0)

# Datasets indices and annotaitons
ds_idxs = range(1, 17)
annotations = {idx: pd.read_csv(DIR_ANNOTATIONS + f"{idx}_annotation.csv", index_col=0) for idx in ds_idxs}

# calculate DOSS between two datasets
def calc_DOSS(i_annotation, j_annotation):
    idxs = [int(el) for el in i_annotation["term_id"] if el != "None"]
    cols = [str(el) for el in j_annotation["term_id"] if el != "None"]
    sim_matrix = sims.loc[idxs, cols]
    max_vector = sim_matrix.apply(max, axis=1)
    return np.mean(max_vector)

# get DOSS matrix
DOSS_matrix = pd.DataFrame(columns=ds_idxs, index=ds_idxs)
for ds_i in ds_idxs:
    for ds_j in ds_idxs:
        DOSS_matrix.loc[ds_i, ds_j] = calc_DOSS(annotations[ds_i], annotations[ds_j])

DOSS_matrix.to_csv("DOSS_matrix.csv")
