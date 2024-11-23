package frc.robot.examples.lambdas;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.XboxController;

public class LambdaExample1 {
  int m_intVal = 10;

  public void printValueOfMemberVariable() {
    System.out.print("The value of X is " + m_intVal);    
  }     

  static public boolean getARandomBool() {    
    return Math.random() < 0.5;
  }

  @SuppressWarnings("unused")
  public static void main(String[] args) {  
      int x = 1;
      String myName = "Your Name";
      XboxController gamepad1 = new XboxController(0);

      // ****  Type 1, direct method/function by name assigment
      LambdaExample1 lex = new LambdaExample1();
      Runnable instanceMethod = lex::printValueOfMemberVariable; 
        
      BooleanSupplier staticMethod = LambdaExample1::getARandomBool; 

      // **** Type 2, lambda to an anonymous function/method
      Runnable runableMethodNoParams = () -> {
          System.out.println("Hello");
          System.out.println("World ");
      };
      
      // Assign a function that always returns a value. Ordinarily your block 
      // of code  would do something more interesting like call a 
      // function or method  to get data from a sensor.
      BooleanSupplier bsBlockWithReturn = () -> { return false; }; 
      DoubleSupplier ds = () -> {
        return 3.0;
      };         

      // **** Type 3, lambda of an expression
      // Similar to type 2, we've just left out "{return ....}" parts
      BooleanSupplier bsExpression = () -> false; 
      DoubleSupplier ds2 = () -> 3.0;


      // These are WRONG, the expression is evaluated at the time of the assigment,
      // it is not within a "block" of code
      BooleanSupplier bsExpressionBad = () -> LambdaExample1.getARandomBool(); 
      BooleanSupplier bsExpressionBad2 = () -> x > 3;      
  }
}
