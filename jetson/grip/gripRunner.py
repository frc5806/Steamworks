import grip
import cv2
camera = cv2.VideoCapture(1)
while True:
    g = grip.GripPipeline()
    capture = camera.read()
    print g.process(capture)

