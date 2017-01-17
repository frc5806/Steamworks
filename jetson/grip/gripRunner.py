import grip
import cv2
camera = cv2.VideoCapture(0)
while True:
    g = grip.GripPipeline()
    s, capture = camera.read()
    cv2.imshow("frame", capture)
