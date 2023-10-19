"""TO BE RUN ON RPI ONLY

A Raspberry Pi test program that uses imagezmq to send image frames from the
PiCamera continuously to a receiving program on a Mac that will display the
images as a video stream.

This program requires that the image receiving program be running first.
Find the program in ../grp1_img_rec/main.py
"""

import sys
import socket
import time
import cv2
from imutils.video import VideoStream
import imagezmq


sender = imagezmq.ImageSender(connect_to='tcp://192.168.1.12:5555')

picam = VideoStream(usePiCamera=True).start()

time.sleep(2.0)  # allow camera sensor to warm up
for i in range(16):
    image = picam.read()
    reply = sender.send_image("Image from RPi", image)
    print(reply)
    print("3...")
    cv2.waitKey(1000)
    print("2...")
    cv2.waitKey(1000)
    print("1...")
    cv2.waitKey(1000)

sender.close()