import cv2
import torch
from pathlib import Path


model_weights = Path('../assets/best-pt/task1-minhtet-v1.pt')

# Load the YOLOv5 model
model = torch.hub.load('ultralytics/yolov5:master', 'custom', path=model_weights)


# Access the webcam feed
cap = cv2.VideoCapture(0)  # 0 for the default camera, you can specify a different camera if needed

while True:
    has_image_detected, frame = cap.read()

    if not has_image_detected:
        print("Error: Failed to capture frame from the webcam.")
        break

    # Perform object detection on the frame
    results = model(frame)

    # Process and display the results
    output_frame = results.render()[0]
    # print(output_frame)
    cv2.imshow('Object Detection', output_frame)

    # Press 'q' to exit the loop
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
# You can replace 'yolov5s' with your custom model path

# Access the webcam feed
cap = cv2.VideoCapture(0)  # 0 for the default camera, you can specify a different camera if needed

while True:
    has_image_detected, frame = cap.read()

    if not has_image_detected:
        print("Error: Failed to capture frame from the webcam.")
        break

    # Perform object detection on the frame
    results = model(frame)

    # Process and display the results
    output_frame = results.render()[0]
    # print(output_frame)
    cv2.imshow('Object Detection', output_frame)

    # Press 'q' to exit the loop
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
