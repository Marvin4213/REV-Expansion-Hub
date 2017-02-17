/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.robotcontroller.internal.testcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * Simply drives in a straight line
 */
@Autonomous(name="Test Straight Line (Linear)", group ="Tests")
@Disabled
public class TestLynxStraightDriveAuto extends LinearOpMode
    {
    // All hardware variables can only be initialized inside the main() function,
    // not here at their member variable declarations.
    DcMotorEx motorLeft = null;
    DcMotorEx motorRight = null;

    @Override
    public void runOpMode() throws InterruptedException
        {
        // Initialize our hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names you assigned during the robot configuration
        // step you did in the FTC Robot Controller app on the phone.
        this.motorLeft = this.hardwareMap.get(DcMotorEx.class, "motorLeft");
        this.motorRight = this.hardwareMap.get(DcMotorEx.class, "motorRight");

        // Configure the knobs of the hardware according to how you've wired your robot.
        DcMotor.RunMode mode = DcMotor.RunMode.RUN_USING_ENCODER;
        this.motorLeft.setMode(mode);
        this.motorRight.setMode(mode);

        // One of the two motors (here, the left) should be set to reversed direction
        // so that it can take the same power level values as the other motor.
        this.motorLeft.setDirection(DcMotor.Direction.REVERSE);

        // Wait until we've been given the ok to go
        this.waitForStart();

        // Drive in a line at various speeds
        for (int degreesPerSecond = 300; degreesPerSecond <= 1000; degreesPerSecond += 100)
            {
            int ticksPerSecond = ticksPerSecFromDegsPerSec(degreesPerSecond);

            telemetry.addData("deg/s", degreesPerSecond);
            telemetry.addData("ticks/s", ticksPerSecond);
            updateTelemetry(telemetry);

            // Drive for a while, then stop
            this.motorLeft.setVelocity(degreesPerSecond, AngleUnit.DEGREES);
            this.motorRight.setVelocity(degreesPerSecond, AngleUnit.DEGREES);

            long msDeadline = System.currentTimeMillis() + 3000;
            while (opModeIsActive() && (System.currentTimeMillis() < msDeadline))
                {
                Thread.yield();
                telemetry.addData("deg/s",   degreesPerSecond);
                telemetry.addData("ticks/s", ticksPerSecond);
                telemetry.addData("left",    motorLeft.getCurrentPosition());
                telemetry.addData("right",   motorRight.getCurrentPosition());
                updateTelemetry(telemetry);
                }

            this.motorRight.setPower(0);
            this.motorLeft.setPower(0);

            Thread.sleep(3000);

            if (isStopRequested())
                break;
            }
        }

    /** Computes the number of encoder ticks per second that correspond to given desired
     * rotational rate. To perform this computation, the number of encoder ticks per second
     * is required, which is motor dependent. We assume left and right motors are of the same type */
    int ticksPerSecFromDegsPerSec(int degreesPerSecond)
        {
        final int encoderTicksPerRevolution = (int)Math.round(this.motorLeft.getMotorType().getTicksPerRev());
        final int degreesPerRevolution      = 360;

        return encoderTicksPerRevolution * degreesPerSecond / degreesPerRevolution;
        }

    }