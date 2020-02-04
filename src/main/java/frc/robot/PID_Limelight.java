package frc.robot;

import edu.wpi.first.wpilibj.command.PIDSubsystem;

public class PID_Limelight extends PIDSubsystem { // This system extends PIDSubsystem

    DifferentialDrive m_robotDrive; 
    double tx;
    //Victor motor = RobotMap.wristMotor;
    //AnalogInput pot = RobotMap.wristPot();

    public PID_Limelight() {
            super("Visiontarget", 2.0, 0.0, 0.0);// The constructor passes a name for the subsystem and the P, I and D constants that are useed when computing the motor output
            setAbsoluteTolerance(0.05);
            getPIDController().setContinuous(false); //manipulating the raw internal PID Controller
    }

    public PID_Limelight(DifferentialDrive m_robotDrive, double tx){
        super("Visiontarget", 2.0, 0.0, 0.0);// The constructor passes a name for the subsystem and the P, I and D constants that are useed when computing the motor output
        setAbsoluteTolerance(0.05);
        this.tx=tx;
        this.m_robotDrive=m_robotDrive;
        getPIDController().setContinuous(false); //manipulating the raw internal PID Controller
}


    public void initDefaultCommand() {
    }

    protected double returnPIDInput() {
        return this.tx; // returns the sensor value that is providing the feedback for the system
    }


    protected void usePIDOutput(double output) {
            m_robotDrive.arcadeDrive(0,output);
        }
}