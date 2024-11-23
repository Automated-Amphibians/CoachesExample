package frc.robot.examples.lambdas;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.subsystems.FakeMotor;



public class LambdaExample2 {
  
  // ChatGPT:  What are some common names of methods in Java for functional interfaces? (like apply, exec)
  interface TwoDoubleFunctionWithReturn<T3, T1, T2> {
    T3 apply(T1 val1, T2 val2);
  }

  /***
   * This section is used to describe the common types of functional classes 
   * used within WPILib. It is not meant to be comprehensive, just what is 
   * common. Ignore the fancy lambda example.
   */
  @SuppressWarnings("unused")
  public static void main(String[] args) {  

  
      TwoDoubleFunctionWithReturn<Double, Double, Double> add = (x, y) -> {
        return x + y;
      };

      FakeMotor motor = new FakeMotor();
      XboxController controller = new XboxController(0);

      // Common types for wpilib commands      
      Runnable startMotor = () -> {
          motor.startMotor();
          System.out.println("World ");
      };
      
      BooleanSupplier isAButtonPressed = controller::getAButtonPressed;
      DoubleSupplier getLeftXAxis = controller::getLeftX;
      DoubleSupplier getRightXAxis = () -> {
        return controller.getRightX();
      };

      // Below is WRONG!!!! Why?
      DoubleSupplier getRightXAxisWrong = () -> controller.getRightX();
      // Hint -- DoubleSupplier getRightXAxisWrong = () -> 0

  }
}
