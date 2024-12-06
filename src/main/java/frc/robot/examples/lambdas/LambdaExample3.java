package frc.robot.examples.lambdas;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.subsystems.FakeMotor;


/**
 * This is intended to show the lambda syntax.
 */
public class LambdaExample3 {

  static FakeMotor motor = new FakeMotor();
  static XboxController controller = new XboxController(0);
  
  static Double simpleReturnOfFive() {
    return 5.0;
  }

  @FunctionalInterface
  public interface Runnable {      
      public abstract void run();      
  }

  /** 
   * This section is intended to describe the syntax of lambda functions.
   */
  @SuppressWarnings("unused")
  public static void main(String[] args) {  

      // () -> signals the compiler to convert the code block into an 
      // instance of the target type, which can then be assigned to a 
      // variable. The code block must match the method's signature 
      // (parameters and return type).

      // () -> tells the compiler a code block or expression is about to 
      // follow, and that it should be used as a function. Standard rules 
      // about matching method/function signatures apply.
      Runnable startMotor = () -> {
          motor.startMotor();          
      };      
      
      // :: (colon colon) tells the compiler that you are asking for the
      // code belonging to the method by that name, NOT to call the method
      BooleanSupplier isAButtonPressed = controller::getAButtonPressed;
      DoubleSupplier getLeftXAxis = controller::getLeftX;
      DoubleSupplier getRightXAxis = () -> {
        // NOTE: the code is not *executed* until this function is called.
        return controller.getRightX();
      };
      
      Runnable r = () -> {System.out.println("Hello World!");};      

  }
}
