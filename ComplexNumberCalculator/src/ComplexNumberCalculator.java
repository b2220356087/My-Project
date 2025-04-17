
public class ComplexNumberCalculator {

    public static class ComplexNumber {
        private final int real;
        private final int imaginary;

        public ComplexNumber(int real, int imaginary) {
            this.real = real;
            this.imaginary = imaginary;
        }

        public int getReal() {
            return real;
        }

        public int getImaginary() {
            return imaginary;
        }
    }



    // Addition
    public ComplexNumber sum(ComplexNumber c1, ComplexNumber c2) {


        int realSum = c1.getReal() + c2.getReal();
        int imaginarySum = c1.getImaginary() + c2.getImaginary();


        return new ComplexNumber(realSum, imaginarySum);
    }

    // Subtraction
    public ComplexNumber sub(ComplexNumber c1, ComplexNumber c2) {


        int realDiff = c1.getReal() - c2.getReal();       // Subtract real parts
        int imaginaryDiff = c1.getImaginary() - c2.getImaginary();  // Subtract imaginary parts

        return new ComplexNumber(realDiff, imaginaryDiff);
    }

    // Multiplication
    public ComplexNumber mul(ComplexNumber c1, ComplexNumber c2) {
        // Bcs of i*i= -1 I used this formula:
        // (a + bi)(c + di) = (a * c - b * d) + (a * d + b * c)i

        int realProduct = (c1.getReal() * c2.getReal()) - (c1.getImaginary() * c2.getImaginary());
        int imaginaryProduct = (c1.getReal() * c2.getImaginary()) + (c1.getImaginary() * c2.getReal());

        return new ComplexNumber(realProduct, imaginaryProduct);
    }

    // Equality check
    public boolean equal(ComplexNumber c1, ComplexNumber c2) {

        boolean realEqual = (c1.getReal() == c2.getReal());
        boolean imaginaryEqual = (c1.getImaginary() == c2.getImaginary());

        return realEqual && imaginaryEqual;
    }
    public static void main(String[] args)  {
        // Getting args
        int real1 = Integer.parseInt(args[0]);
        int imag1 = Integer.parseInt(args[1]);
        int real2 = Integer.parseInt(args[2]);
        int imag2 = Integer.parseInt(args[3]);
        String operation = args[4]; // "add", "sub", "mul","equal"


        ComplexNumber c1 = new ComplexNumber(real1, imag1);
        ComplexNumber c2 = new ComplexNumber(real2, imag2);


        ComplexNumberCalculator calculator = new ComplexNumberCalculator();

        // I used switch/case bcs there is not more option and it seems clear then if/else
        switch (operation) {
            case "add":
                ComplexNumber sumResult = calculator.sum(c1, c2);
                System.out.println(sumResult.getReal() + "+" + sumResult.getImaginary() + "i");
                break;
            case "sub":
                ComplexNumber subResult = calculator.sub(c1, c2);
                System.out.println(subResult.getReal() + "+" + subResult.getImaginary() + "i");
                break;
            case "mul":
                ComplexNumber mulResult = calculator.mul(c1, c2);
                System.out.println(mulResult.getReal() + "+" + mulResult.getImaginary() + "i");
                break;
            case "equal":
                boolean isEqual = calculator.equal(c1, c2);
                System.out.println(isEqual);
                break;

            }
        }
}
