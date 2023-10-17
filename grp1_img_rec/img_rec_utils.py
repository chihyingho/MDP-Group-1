import tkinter as tk
import PIL
import cv2
import numpy as np
from PIL import Image, ImageTk
from torchvision import transforms


class NoObjectDetectedException(Exception):
    def __init__(self, message):
        super().__init__(message)


LABELS = {"11": "1", "12": "2", "13": "3", "14": "4", "15": "5",
          "16": "6", "17": "7", "18": "8", "19": "9",
          "20": "A", "21": "B", "22": "C", "23": "D", "24": "E",
          "25": "F", "26": "G", "27": "H", "28": "S", "29": "T",
          "30": "U", "31": "V", "32": "W", "33": "X", "34": "Y", "35": "Z",
          "36": "up", "37": "down", "38": "right", "39": "left",
          "40": "stop", "99": "bullseye"}
NO_ID_STRING = "None"


def detect_objects(model, image, minimum_confidence=0.4):
    """
    Returns a dataframe of probabilities after running a YOLOv5 model against an image.
    :param minimum_confidence: Minimum
    :param model:
    :param image:
    :return: Pandas dataframe of (`xmin`, `ymin`, `xmax`, `ymax`, `confidence`, `class`, `name`)
    :raises NoObjectDetectedException: if there is no object detected

    `name` column refers to the ID assigned to the class in the model training process (here it is 11, 12...) whereas
    the `class` column refers to the index of the column.
    """
    results = model(image)
    results_df = results.pandas().xyxy[0]
    if not results_df.empty:
        print(results_df)
    results_df = results_df[results_df['confidence'] > minimum_confidence]
    if results_df.empty:
        raise NoObjectDetectedException("No object detected!")
    results_df.sort_values(by=['confidence'], ascending=False, inplace=True)
    return results_df


def resize_with_padding(image, target_size):
    """
    Resize the input image while preserving the aspect ratio and adding white padding all around if needed.
    :param image: Input CV2 image (BGR format).
    :param target_size: Tuple of (H, W) representing the target height and width.
    :return: Resized and padded image as a CV2 image (BGR format).
    """
    # Convert CV2 image to RGB format
    image_rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

    # Calculate the aspect ratio
    aspect_ratio = image.shape[1] / image.shape[0]

    # Calculate the new dimensions to fit within the target size
    new_width = target_size[1]
    new_height = int(new_width / aspect_ratio)

    if new_height > target_size[0]:
        # Resize the height to fit within the target size
        new_height = target_size[0]
        new_width = int(new_height * aspect_ratio)

    # Calculate padding values
    pad_height = target_size[0] - new_height
    pad_width = target_size[1] - new_width

    # Calculate top and left padding
    top_padding = pad_height // 2
    left_padding = pad_width // 2

    # Resize the image
    transform = transforms.Compose([
        transforms.ToPILImage(),
        transforms.Resize((new_height, new_width)),
        transforms.Pad((left_padding, top_padding, pad_width - left_padding, pad_height - top_padding), fill=255)
    ])

    resized_image = transform(image_rgb)

    resized_image_bgr = cv2.cvtColor(np.array(resized_image), cv2.COLOR_RGB2BGR)

    return resized_image_bgr


def annotate(image, top_result):
    """
    Annotates an image with the result from `select_best_object` and returns the top ID recognised.
    :param image: Input CV2 image (BGR format).
    :param top_result: Dataframe of only one row, headers = (xmin, ymin, xmax, ymax, confidence, class, name)
    :return: image with additional text for object detected and its ID

    Skips annotation and returns 0 if there is no result found.
    """
    if top_result.empty:
        raise Exception("Need to pass in at least one valid object!")
    top_result = top_result.iloc[0]
    xmin, ymin, xmax, ymax = (int(top_result['xmin']),
                              int(top_result['ymin']),
                              int(top_result['xmax']),
                              int(top_result['ymax']))
    cv2.rectangle(image,
                  (xmin, ymin),
                  (xmax, ymax),
                  color=(0, 255, 0),
                  thickness=2)
    image_id = str(int(top_result['name']))
    annotated_text = f"ID: {image_id} Name: {LABELS.get(image_id, 'ERROR')}"
    cv2.putText(image, annotated_text,
                (10, 30),
                cv2.FONT_HERSHEY_SIMPLEX, 1,
                (0, 255, 0),
                2)
    return image, image_id


def display_image_on_canvas(canvas, image):
    image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)  # Convert BGR to RGB
    image = Image.fromarray(image)  # Convert to PIL Image
    photo = ImageTk.PhotoImage(image)  # Convert to PhotoImage

    # Create a label to display the image on the canvas
    label = tk.Label(canvas, image=photo)
    label.photo = photo  # To prevent the image from being garbage collected
    label.grid(row=canvas.row_index, column=canvas.col_index, padx=10, pady=10)

    # Update the grid indices
    canvas.col_index += 1
    if canvas.col_index > 3:
        canvas.col_index = 0
        canvas.row_index += 1


def cv2_to_tkinter(annotated_image):
    display_img = cv2.cvtColor(annotated_image, cv2.COLOR_BGR2RGB)
    display_img = PIL.Image.fromarray(display_img)
    display_img = ImageTk.PhotoImage(display_img)
    return display_img


def are_images_equal(image1, image2):
    """
    Custom assertion method to compare two OpenCV images.
    """
    if image1.shape != image2.shape:
        return False
    difference = cv2.subtract(image1, image2)
    b, g, r = cv2.split(difference)
    is_equal = cv2.countNonZero(b) == 0 and cv2.countNonZero(g) == 0 and cv2.countNonZero(r) == 0
    return is_equal


def select_best_object(df, threshold=0.8):
    """
    If there is more than one object detected, select the object with the largest bounding box above a specified
    confidence threshold.
    :param df: Dataframe of any number of rows, headers = `(xmin, ymin, xmax, ymax, confidence, class, name)`
    :param threshold:
    :return: Subset of a df row that has the best entry,
        headers = `(xmin, ymin, xmax, ymax, confidence, class, name, area)`
    """
    if df.empty:
        raise NoObjectDetectedException("No object detected!")
    df = df.sort_values(by='confidence', ascending=False)
    df['area'] = (df['xmax'] - df['xmin']) * (df['ymax'] - df['ymin'])
    if df.shape[0] > 1:
        # if too many rows, pick the top one
        df_above_threshold = df[df['confidence'] > threshold]
        if df_above_threshold.shape[0] > 1:
            df = df_above_threshold.sort_values(by='area', ascending=False)
    return df.drop(df.index[1:]).reset_index(drop=True)
