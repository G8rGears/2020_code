/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import com.ctre.phoenix.motorcontrol.NeutralMode;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */

public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private DoubleSolenoid Hanger;
  private XboxController mainXboxController;
  private XboxController intakeController; 
  private int autoX = 0;
  private int autoY = 0;
  WPI_VictorSPX _frontLeftMotor = new WPI_VictorSPX(2);
  WPI_VictorSPX _frontRightMotor = new WPI_VictorSPX(4);
  WPI_VictorSPX _leftFollower = new WPI_VictorSPX(1);
  WPI_VictorSPX _rightFollower = new WPI_VictorSPX(3);
  private final DifferentialDrive m_robotDrive = new DifferentialDrive(_frontLeftMotor,_frontRightMotor);
  private final Limelight limelight = Limelight.getInstance();
  WPI_VictorSPX intakeLeader = new WPI_VictorSPX(5);
  WPI_VictorSPX intakeFollower = new WPI_VictorSPX(6);
  WPI_TalonSRX shooterLeader = new WPI_TalonSRX(1);
  WPI_TalonSRX shooterFollower = new WPI_TalonSRX(2);

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */

  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    _rightFollower.follow(_frontRightMotor);
    _leftFollower.follow(_frontLeftMotor); 
    intakeFollower.follow(intakeLeader);
    shooterFollower.follow(shooterLeader);
    

    Hanger = new DoubleSolenoid(0, 1); 
    mainXboxController = new XboxController(0); 
    intakeController = new XboxController(1);

    _frontLeftMotor.setNeutralMode(NeutralMode.Brake);
    _frontRightMotor.setNeutralMode(NeutralMode.Brake);
    _rightFollower.setNeutralMode(NeutralMode.Brake);
    _leftFollower.setNeutralMode(NeutralMode.Brake);
    intakeFollower.setNeutralMode(NeutralMode.Brake);
    intakeLeader.setNeutralMode(NeutralMode.Brake);
    shooterFollower.setNeutralMode(NeutralMode.Coast);
    shooterLeader.setNeutralMode(NeutralMode.Coast);

    _frontLeftMotor.setInverted(false);
    _frontRightMotor.setInverted(false);
    _rightFollower.setInverted(false);
    _leftFollower.setInverted(false);
    intakeFollower.setInverted(true);
    intakeLeader.setInverted(true);
    shooterLeader.setInverted(false);
    shooterFollower.setInverted(false);

    //PID_Limelight pidx = new PID_Limelight();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    m_robotDrive.arcadeDrive(mainXboxController.getY(Hand.kLeft), mainXboxController.getX(Hand.kRight));

    if (mainXboxController.getAButton()) {
      Hanger.set(DoubleSolenoid.Value.kForward);
    }
    else {
      Hanger.set(DoubleSolenoid.Value.kReverse);
    }

    Double intakeLeft = intakeController.getTriggerAxis(Hand.kLeft);
    Double intakeRight = intakeController.getTriggerAxis(Hand.kRight);
    if(intakeLeft > 0 && intakeRight == 0){
      intakeLeader.setInverted(false);
      intakeFollower.setInverted(false);
      intakeLeader.set(intakeController.getTriggerAxis(Hand.kLeft));
    }
    else {
      intakeLeader.setInverted(true);
      intakeFollower.setInverted(true);
      intakeLeader.set(intakeController.getTriggerAxis(Hand.kRight));
    }

    Double mainXboxControllerLeft = mainXboxController.getTriggerAxis(Hand.kLeft);
    Double mainXboxControllerRight = mainXboxController.getTriggerAxis(Hand.kRight);
    if(mainXboxControllerLeft > 0 && mainXboxControllerRight == 0){
      shooterLeader.setInverted(true);
      shooterFollower.setInverted(true);
      shooterLeader.set(mainXboxController.getTriggerAxis(Hand.kLeft));
    }
    else {
      shooterLeader.setInverted(false);
      shooterFollower.setInverted(false);
      shooterLeader.set(mainXboxController.getTriggerAxis(Hand.kRight));
    }

    /*
    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    NetworkTableEntry tx = table.getEntry("tx");
    NetworkTableEntry tv = table.getEntry("tv");
    NetworkTableEntry ty = table.getEntry("ty");
    NetworkTableEntry ta = table.getEntry("ta");

    //read values periodically
    double x = tx.getDouble(0.0);
    double y = ty.getDouble(0.0);
    double area = ta.getDouble(0.0);

    //post to smart dashboard periodically
    SmartDashboard.putNumber("LimelightX", x);
    SmartDashboard.putNumber("LimelightY", y);
    SmartDashboard.putNumber("LimelightArea", area);
    */
    autoX = 0;
    autoY = 0;
  }

  /**
   * This function is called periodically during test mode.
   */
  /**@Override
  public void testPeriodic() {
  }
  public DifferentialDrive getMotors() {
    
  } */
}


