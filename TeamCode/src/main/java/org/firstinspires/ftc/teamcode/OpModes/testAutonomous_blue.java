//package org.firstinspires.ftc.teamcode.OpModes;
//
//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.hardware.ColorSensor;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.Servo;
//
//import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
//import org.opencv.core.*;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.imgproc.Moments;
//import org.openftc.easyopencv.OpenCvCamera;
//import org.openftc.easyopencv.OpenCvCameraFactory;
//import org.openftc.easyopencv.OpenCvCameraRotation;
//import org.openftc.easyopencv.OpenCvPipeline;
//
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Autonomous(name = "testAutonomous_Blue")
//
//public class testAutonomous_blue extends LinearOpMode {
//    Hware robot;
//    Servo rightClaw;
//    Servo launcher;
//    Servo leftClaw;
//    Servo wristLeft;
//    Servo wristRight;
//    double cX = 0;
//    double cY = 0;
//    double width = 0;
//    double ticks = 537.7;
//    double newTarget;
//    private OpenCvCamera controlHubCam;  // Use OpenCvCamera class from FTC SDK
//    private static final int CAMERA_WIDTH = 1280; // width  of wanted camera resolution
//    private static final int CAMERA_HEIGHT = 960; // height of wanted camera resolution
//    // Calculate the distance using the formula
//    public static final double objectWidthInRealWorldUnits = 3.75;  // Replace with the actual width of the object in real-world units
//    public static final double focalLength = 728;  // Replace with the focal length of the camera in pixels
//
//
//    @Override
//    public void runOpMode() {
//        robot = new Hware(hardwareMap);
//        initOpenCV();
//        waitForStart();
//
//
//        FtcDashboard dashboard = FtcDashboard.getInstance();
//        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());
//        FtcDashboard.getInstance().startCameraStream(controlHubCam, 30);
//
//        telemetry.addData("Coordinate", "(" + (int) cX + ", " + (int) cY + ")");
//        telemetry.addData("Distance in Inch", (getDistance(width)));
//        telemetry.update();
//
//
//        //clawClose();
//        //wristUp();
//        while (opModeIsActive()) {
//
//            if(cX < 320)
//            {
//                telemetry.addData("Direction: ", "left");
//                leftPath();
//            }
//            else if (cX > 960)
//            {
//                telemetry.addData("Direction: ", "right");
//                rightPath();
//            }
//            else if(cX>320 && cX<960)
//            {
//                telemetry.addData("Direction: ", "center");
//                forwardPath();
//            } else {
//                forwardPath();
//            }
//
//            // The OpenCV pipeline automatically processes frames and handles detection
//        }
//
//        // Release resources
//        controlHubCam.stopStreaming();
//    }
//
//    private void initOpenCV() {
//
//        // Create an instance of the camera
//        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
//                "cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
//
//        // Use OpenCvCameraFactory class from FTC SDK to create camera instance
//        controlHubCam = OpenCvCameraFactory.getInstance().createWebcam(
//                hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
//
//        controlHubCam.setPipeline(new YellowBlobDetectionPipeline());
//
//        controlHubCam.openCameraDevice();
//        controlHubCam.startStreaming(CAMERA_WIDTH, CAMERA_HEIGHT, OpenCvCameraRotation.UPRIGHT);
//    }
//    class YellowBlobDetectionPipeline extends OpenCvPipeline {
//        @Override
//        public Mat processFrame(Mat input) {
//            // Preprocess the frame to detect yellow regions
//            Mat yellowMask = preprocessFrame(input);
//
//            // Find contours of the detected yellow regions
//            List<MatOfPoint> contours = new ArrayList<>();
//            Mat hierarchy = new Mat();
//            Imgproc.findContours(yellowMask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//
//            // Find the largest yellow contour (blob)
//            MatOfPoint largestContour = findLargestContour(contours);
//
//            if (largestContour != null) {
//                // Draw a red outline around the largest detected object
//                Imgproc.drawContours(input, contours, contours.indexOf(largestContour), new Scalar(255, 0, 0), 2);
//                // Calculate the width of the bounding box
//                width = calculateWidth(largestContour);
//
//                // Display the width next to the label
//                String widthLabel = "Width: " + (int) width + " pixels";
//                Imgproc.putText(input, widthLabel, new Point(cX + 10, cY + 20), Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 255, 0), 2);
//                //Display the Distance
//                String distanceLabel = "Distance: " + String.format("%.2f", getDistance(width)) + " inches";
//                Imgproc.putText(input, distanceLabel, new Point(cX + 10, cY + 60), Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 255, 0), 2);
//                // Calculate the centroid of the largest contour
//                Moments moments = Imgproc.moments(largestContour);
//                cX = moments.get_m10() / moments.get_m00();
//                cY = moments.get_m01() / moments.get_m00();
//
//                // Draw a dot at the centroid
//                String label = "(" + (int) cX + ", " + (int) cY + ")";
//                Imgproc.putText(input, label, new Point(cX + 10, cY), Imgproc.FONT_HERSHEY_COMPLEX, 0.5, new Scalar(0, 255, 0), 2);
//                Imgproc.circle(input, new Point(cX, cY), 5, new Scalar(0, 255, 0), -1);
//
//            }
//
//            return input;
//        }
//
//        private Mat preprocessFrame(Mat frame) {
//            Mat hsvFrame = new Mat();
//            Imgproc.cvtColor(frame, hsvFrame, Imgproc.COLOR_BGR2HSV);
//
//            Scalar lowerYellow = new Scalar(0, 100, 100);
//            Scalar upperYellow = new Scalar(100, 255, 255);
//
//
//            Mat yellowMask = new Mat();
//            Core.inRange(hsvFrame, lowerYellow, upperYellow, yellowMask);
//
//            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
//            Imgproc.morphologyEx(yellowMask, yellowMask, Imgproc.MORPH_OPEN, kernel);
//            Imgproc.morphologyEx(yellowMask, yellowMask, Imgproc.MORPH_CLOSE, kernel);
//
//            return yellowMask;
//        }
//
//        private MatOfPoint findLargestContour(List<MatOfPoint> contours) {
//            double maxArea = 0;
//            MatOfPoint largestContour = null;
//
//            for (MatOfPoint contour : contours) {
//                double area = Imgproc.contourArea(contour);
//                if (area > maxArea) {
//                    maxArea = area;
//                    largestContour = contour;
//                }
//            }
//
//            return largestContour;
//        }
//        private double calculateWidth(MatOfPoint contour) {
//            Rect boundingRect = Imgproc.boundingRect(contour);
//            return boundingRect.width;
//        }
//
//    }
//    private static double getDistance(double width){
//        double distance = (objectWidthInRealWorldUnits * focalLength) / width;
//        return distance;
//    }
//
//    public void encoderLF(int turnage)
//    {
//        robot.leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        newTarget = ticks * turnage;
//        robot.leftFront.setTargetPosition((int)newTarget);
//        robot.leftFront.setPower(0.5);
//        robot.leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//    }
//    public void encoderRF(int turnage)
//    {
//        robot.rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        newTarget = ticks * turnage;
//        robot.rightFront.setTargetPosition((int)newTarget);
//        robot.rightFront.setPower(0.5);
//        robot.rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//    }
//    public void encoderLB(int turnage)
//    {
//        robot.leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        newTarget = ticks * turnage;
//        robot.leftBack.setTargetPosition((int)newTarget);
//        robot.leftBack.setPower(0.5);
//        robot.leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//    }
//    public void encoderRB(int turnage)
//    {
//        robot.rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        newTarget = ticks * turnage;
//        robot.rightBack.setTargetPosition((int)newTarget);
//        robot.rightBack.setPower(0.5);
//        robot.rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//    }
//
//    public void leftPath()
//    {
//        encoderRB(4);
//        encoderRF(4);
//        encoderLB(4);
//        encoderLF(4);
//
//        sleep(1000);
//
//        encoderRB(1);
//        encoderRF(-1);
//        encoderLB(-1);
//        encoderLF(1);
//
//        sleep(1000);
//
//        //wristDown();
//        sleep(1000);
//        //clawOpen(); // make the rightClawOpen if we have time
//
//        sleep(1000);
//
//        encoderRB(-1);
//        encoderRF(1);
//        encoderLB(1);
//        encoderLF(-1);
//
//        sleep(1000);
//
//        encoderRB(2);
//        encoderRF(2);
//        encoderLB(2);
//        encoderLF(2);
//
//        sleep(1000);
//
//        encoderRB(12);
//        encoderRF(-12);
//        encoderLB(-12);
//        encoderLF(12);
//
//    }
//
//    public void forwardPath()
//    {
//        encoderRB(4);
//        encoderRF(4);
//        encoderLB(4);
//        encoderLF(4);
//
//        sleep(1000);
//
//        encoderRB(1);
//        encoderRF(1);
//        encoderLB(1);
//        encoderLF( 1);
//
//        sleep(1000);
//
//
//
//        //wristDown();
//        sleep(1000);
//        //clawOpen(); // make the rightClawOpen if we have time
//
//        sleep(1000);
//
//        encoderRB(-1);
//        encoderRF(-1);
//        encoderLB(-1);
//        encoderLF( -1);
//
//        sleep(1000);
//
//        encoderRB(2);
//        encoderRF(2);
//        encoderLB(2);
//        encoderLF(2);
//
//        sleep(1000);
//
//        encoderRB(4);
//        encoderRF(-4);
//        encoderLB(-4);
//        encoderLF(4);
//    }
//    public void rightPath()
//    {
//        encoderRB(4);
//        encoderRF(4);
//        encoderLB(4);
//        encoderLF(4);
//
//        sleep(1000);
//
//        encoderRB(-1);
//        encoderRF(1);
//        encoderLB(1);
//        encoderLF(-1);
//
//        sleep(1000);
//
//        //wristDown();
//        sleep(1000);
//        //clawOpen(); // make the rightClawOpen if we have time
//
//        sleep(1000);
//
//        encoderRB(1);
//        encoderRF(-1);
//        encoderLB(-1);
//        encoderLF(1);
//
//        sleep(1000);
//
//        encoderRB(2);
//        encoderRF(2);
//        encoderLB(2);
//        encoderLF(2);
//
//
//
//        encoderRB(4);
//        encoderRF(-4);
//        encoderLB(-4);
//        encoderLF(4);
//    }
//}
//
//
