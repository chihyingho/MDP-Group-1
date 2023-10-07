"""test_1_send_images.py -- basic send images test.

A simple test program that uses imagezmq to send images to a receiving program
that will display the images.

This program requires that the image receiving program to be running first.
Brief test instructions are in that program: test_1_receive_images.py.
"""

import cv2
import imagezmq

# Create 2 different test images to send
# A green square on a black background
# A red square on a black background

sender = imagezmq.ImageSender()

image_paths = ['in/imageA.jpg', 'in/imageB.jpg', 'in/imageC.jpg',
               'in/imageD.jpg', 'in/imageE.jpg', 'in/imageF.jpg',
               'in/imageA.jpg', 'in/imageB.jpg', 'in/imageC.jpg',
               'in/imageD.jpg', 'in/imageE.jpg', 'in/imageF.jpg']

for image_path in image_paths:
    # Read the image from the file
    image = cv2.imread(image_path)

    # Send the image
    reply = sender.send_image(msg=image_path, image=image)

    print(reply)

    # Optionally, add a delay between sending images
    # You can adjust the delay as needed
    cv2.waitKey(10)  # Wait for 1 second (1000 milliseconds)

sender.close()
