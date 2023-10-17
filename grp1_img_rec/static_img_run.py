from pathlib import Path

import cv2
import torch
import time

import config
import main
from img_rec_utils import NoObjectDetectedException, detect_objects, annotate, resize_with_padding


# LOAD MODEL
PATH_MODEL_WEIGHTS = Path(config.PATH_MODEL_WEIGHTS)

model = main.setup_model(for_task2=True)

# LOAD IMAGE
image = cv2.imread("/Users/shunyao/uni/mdp/assets/39-indoors/20230825_122536.jpg")
# image = cv2.imread("in/imageA.jpg")

# PREPROCES
image = resize_with_padding(image, config.IMG_DIMENSIONS)

# DETECT AND ANNOTATE IMAGE
try:
    labels_df = detect_objects(model, image)
    image, label = annotate(image, labels_df)
    print(f"Image recognised: {label}")
except NoObjectDetectedException as e:
    print("No image detected!")


# Save the image with detected objects
cv2.imwrite('out/adhoc_run_image.jpg', image)
