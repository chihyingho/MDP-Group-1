import torch
import imagezmq
from config import *
from img_rec_utils import *
from pathlib import Path


def main():
    model = setup_model(for_task2=True)
    image_hub = setup_imagezmq()
    image_labels, window = setup_tkinter()

    for i in range(EXPECTED_IMAGES):
        message, image = image_hub.recv_image()
        cv2.imwrite(OUT_RAW_IMAGE_PATH + f"{i + 1}.jpg", image)
        downsampled_image = resize_with_padding(image, IMG_DIMENSIONS)

        try:
            labels_df = detect_objects(model, downsampled_image)
            labels_df = select_best_object(labels_df)

        except NoObjectDetectedException as e:
            print(DEBUG_HEADER + "No object detected!")
            image_hub.send_reply(b"No object detected!")

            cv2.imwrite(OUT_IMAGE_PATH + f'{i + 1}.jpg', downsampled_image)
            display_img = resize_with_padding(downsampled_image, SIZE_FOR_DISPLAY)

        else:
            annotated_image, label = annotate(downsampled_image, labels_df)
            print(DEBUG_HEADER + f"Image {i + 1} recognized: {label}")
            reply = f"{label}"
            image_hub.send_reply(reply.encode('utf-8'))

            cv2.imwrite(OUT_IMAGE_PATH + f'{i}.jpg', annotated_image)
            display_img = resize_with_padding(annotated_image, SIZE_FOR_DISPLAY)

        display_img = cv2_to_tkinter(display_img)
        add_to_tkinter(display_img, i, image_labels, window)

    # Start the Tkinter main loop
    window.mainloop()


def add_to_tkinter(display_img, i, image_labels, window):
    """
    Displays images in rows of 4 images.
    :param display_img:
    :param i:
    :param image_labels:
    :param window:
    :return:
    """
    image_labels.append(display_img)
    image_label = tk.Label(master=window, image=display_img)
    image_label.grid(row=i // 4, column=i % 4, padx=10, pady=10)


def setup_tkinter():
    window = tk.Tk()
    window.title("Processed Images")
    print(DEBUG_HEADER + "Tkinter ready")
    image_labels = []  # stores PhotoImage objects (prevents gc)
    return image_labels, window


def setup_imagezmq():
    image_hub = imagezmq.ImageHub()
    print(DEBUG_HEADER + "ImageZMQ Server Live at: " + TCP_ADDRESS)
    return image_hub


def setup_model(for_task2=False):
    """
    :param for_task2: Filters image rec to only detect left and right arrows (only detects class name 38 and 39)
    :return:
    """
    model = torch.hub.load('../yolov5', 'custom', path=Path(PATH_MODEL_WEIGHTS), source='local')
    if for_task2:
        model.classes = [27, 28]
        # Note: model.classes takes in index of class names, not class ID, specifically for this model:
        # class index 27 is right arrow (yolov5 name: 38), 28 is left arrow (yolov5 name: 39)
    model.eval()
    print(DEBUG_HEADER + "Model Loaded: " + PATH_MODEL_WEIGHTS)
    return model


if __name__ == "__main__":
    main()
