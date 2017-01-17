import grip
import cv2
while True:
    g = grip.GripPipeline()
    capture = cv2.CaptureFromCAM(1)
    print g.process(capture)

