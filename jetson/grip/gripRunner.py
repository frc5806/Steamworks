import grip
import cv2
camera = cv2.VideoCapture(1)
cv2.namesWindow("window", cv2.WINDOW_NORMAL)
while True:
    g = grip.GripPipeline()
    capture = camera.read()
    cv2.imshow("window", capture)
