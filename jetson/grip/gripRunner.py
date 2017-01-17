import grip
import cv2
camera = cv2.VideoCapture(0)
cv2.namedWindow("window", cv2.WINDOW_NORMAL)
while True:
    g = grip.GripPipeline()
    capture = camera.read()
    cv2.imshow("window", capture)
