package frc.robot.examples.lambdas;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;

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
      // ****  Type 1, direct method/function by name assignment (not a Lambda expression!)
      LambdaExample1 lex = new LambdaExample1();
      Runnable instanceMethod = lex::printValueOfMemberVariable;
       
      BooleanSupplier staticMethod = LambdaExample1::getARandomBool;
      

      // **** Type 2, code block lambda expressions 
      Runnable runableMethodNoParams = () -> {
          System.out.print("Hello ");
          System.out.println("World");
      };
      runableMethodNoParams.run();
     
      // Lambda expressions that return a value. 
      // Ordinarily your block of code  would do something more 
      // interesting like call a function or method to get data from a sensor.
      BooleanSupplier bsBlockWithReturn = () -> { return false; };
      DoubleSupplier randomDouble = () -> {
        return Math.random();
      };
      System.out.println(randomDouble.getAsDouble());

      // **** Type 3, lambda expression, no parenthesis
      // Similar to type 2, we've just left out "{return ....}" parts
      BooleanSupplier bsExpression = () -> Math.random() > 0.5;
      DoubleSupplier randomDouble2 = () -> Math.random();
      System.out.println(bsExpression.getAsBoolean());            
      System.out.println(randomDouble2.getAsDouble());
      DoubleConsumer fc = null;

  }
}
