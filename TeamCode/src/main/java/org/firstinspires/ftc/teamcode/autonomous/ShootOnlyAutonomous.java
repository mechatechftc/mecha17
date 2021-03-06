package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.util.DcMotorPair;

import java.util.Locale;

/**
 * Created by Richik SC on 12/11/2016.
 */

@Autonomous(name = "Shoot Balls and Park on Center Base Delayed", group = "Comp")
public class ShootOnlyAutonomous extends LinearOpMode {

  private static final int PULSES_PER_MOTOR_REV = 28;
  private static final double DRIVE_GEAR_REDUCTION = 40.0;
  private static final double WHEEL_DIAMETER_INCHES = 4;
  private static final double     PULSES_PER_INCH         = (PULSES_PER_MOTOR_REV *
      DRIVE_GEAR_REDUCTION) /
      (WHEEL_DIAMETER_INCHES * 3.1415);

  // Main drive motors
  private DcMotor motorFL;
  private DcMotor motorFR;
  private DcMotor motorBL;
  private DcMotor motorBR;

  // Accessory motors
  private DcMotorPair shooterMotors;
  private DcMotor conveyorMotor;

  private int newTargetFL;
  private int newTargetFR;
  private int newTargetBL;
  private int newTargetBR;

  private void shootTwoBalls() {
    if (opModeIsActive()) {
      // Shoot two balls block

      // Warm up 'Big Bertha' (shooter motors)
      shooterMotors.setPower(0.8);
      sleep(2000);
      //setPowerZero();
      conveyorMotor.setPower(0.8);
      sleep(2000);
      //setPowerZero();
      shooterMotors.setPower(0);
      conveyorMotor.setPower(0);
      //setPowerZero();
    }
  }

  private void moveVertical(double dist) {
    resetEncoders();
    if (opModeIsActive()) {
      int ticks = calculateTargetTicks(dist);
      motorFL.setTargetPosition(ticks);
      motorFR.setTargetPosition(-1*ticks);
      motorBL.setTargetPosition(ticks);
      motorBR.setTargetPosition(-1*ticks);

      if(dist > 0)
        drive(0, 0.25f, 0);
      else
        drive(0, -0.25f, 0);
      // keep looping while we are still active, and there is time left, and both motors are running.
      while (opModeIsActive() &&
          (motorBL.isBusy() && motorFR.isBusy())) {

        // Display it for the driver.
        telemetry.addData("Path1", "Running to %7d", ticks);
        telemetry.addData("Path2", "Running at %7d :%7d :%7d :%7d",
            motorFL.getCurrentPosition(),
            motorFR.getCurrentPosition(),
            motorBL.getCurrentPosition(),
            motorBR.getCurrentPosition()
        );
        telemetry.update();


      }
      setPowerZero();
      resetEncoders();
    }
  }

  private void resetEncoders() {
    motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    motorFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    motorBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    motorBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    idle();

    while (
        motorFL.getCurrentPosition() != 0 &&
            motorFR.getCurrentPosition() != 0 &&
            motorBL.getCurrentPosition() != 0 &&
            motorBR.getCurrentPosition() != 0
        ) {
      sleep(500);
    }


    motorFL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    motorFR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    motorBL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    motorBR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

    idle();

    newTargetFL = 0;
    newTargetFR = 0;
    newTargetBL = 0;
    newTargetBR = 0;

  }

  private int calculateTargetTicks(double targetInches) {
    return (int)Math.round(targetInches * PULSES_PER_INCH);
  }

  private void setPowerZero() {
    motorFL.setPower(0);
    motorFR.setPower(0);
    motorBL.setPower(0);
    motorBR.setPower(0);
  }

  public void drive(float xVal, float yVal, float rotVal) {

    // Holonomic formulas

    float frontLeft = -yVal - xVal - rotVal;
    float frontRight = yVal - xVal - rotVal;
    float backRight = yVal + xVal - rotVal;
    float backLeft = -yVal + xVal - rotVal;

    // Clip the right/left values so that the values never exceed +/- 1
    frontRight = Range.clip(frontRight, -1, 1);
    frontLeft = Range.clip(frontLeft, -1, 1);
    backLeft = Range.clip(backLeft, -1, 1);
    backRight = Range.clip(backRight, -1, 1);
    telemetry.addData("Wheel Value Key", "(Front Left, Front Right, Back Left, Back Right)");
    telemetry.addData("Wheel Values (theoretical)",
        String.format(Locale.US, "(%d, %d, %d, %d)",
            (long)frontLeft,
            (long)frontRight,
            (long)backLeft,
            (long)backRight
        )
    );
    telemetry.update();

    // Write the values to the motors
    motorFL.setPower(frontLeft);
    motorFR.setPower(frontRight);
    motorBL.setPower(backLeft);
    motorBR.setPower(backRight);

  }

  @Override
  public void runOpMode() throws InterruptedException {
    custom_init();
    waitForStart();
    sleep(5000);
    shootTwoBalls();
    sleep(10000);
    moveVertical(-65);
  }

  public void custom_init() {

    // Define and configure motors.
    motorFL = hardwareMap.dcMotor.get("motor_fl");
    motorFR = hardwareMap.dcMotor.get("motor_fr");
    motorBL = hardwareMap.dcMotor.get("motor_bl");
    motorBR = hardwareMap.dcMotor.get("motor_br");

    motorFL.setDirection(DcMotor.Direction.FORWARD);
    motorBL.setDirection(DcMotor.Direction.FORWARD);
    motorFR.setDirection(DcMotor.Direction.FORWARD);
    motorBR.setDirection(DcMotor.Direction.FORWARD);

    motorFL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    motorBL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    motorFR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    motorBR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    // Define accessory hardware
    shooterMotors = new DcMotorPair(
        hardwareMap.dcMotor.get("shooter_l"),
        hardwareMap.dcMotor.get("shooter_r")
    );
    conveyorMotor = hardwareMap.dcMotor.get("conveyor");
    idle();
    resetEncoders();
    telemetry.addData("PPI", PULSES_PER_INCH);
    telemetry.addData("Encoders", "Reset");
    telemetry.addData("Status", "Initialized");
    telemetry.update();
  }

}
