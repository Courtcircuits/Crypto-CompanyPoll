import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class LaGrange {
    private static BigInteger polynome(BigInteger[] coefs, BigInteger x, BigInteger p){
        BigInteger result = BigInteger.ZERO;
        for(int i=0; i< coefs.length; i++){
            result = result.add((x.pow(i)).multiply(coefs[coefs.length-1-i]));
        }
        return result.mod(p);
    }

    public static BigInteger L(List<BigInteger> all, int id, BigInteger x, BigInteger p){
        BigInteger l = BigInteger.ONE;
        BigInteger xi = all.get(id);
        BigInteger num;
        BigInteger denom;
        BigInteger divide;
        for(int j=0; j<all.size(); j++){
            if(id!=j){
                num = x.subtract(all.get(j));
                denom = xi.subtract(all.get(j));
                divide = num.divide(denom);
                l = l.multiply(divide);
            }
        }
        return l.mod(p);
    }


    public static void main(String[] args) {
        BigInteger p = BigInteger.valueOf(13);
        List<BigInteger> y = new ArrayList<>();
        List<BigInteger> x = new ArrayList<>();
        BigInteger[] coefs = {BigInteger.valueOf(3), BigInteger.valueOf(4), BigInteger.valueOf(3)};
        //3x^2 + 3x + 3
        for(int i=0; i<3; i++){
            x.add(BigInteger.valueOf(i));
            y.add(polynome(coefs, x.get(i), p));
        }

        //calcul de l(x)

        BigInteger l = BigInteger.ZERO;
        for(int i=0; i<coefs.length; i++){
            l = l.add(y.get(i).multiply(L(x, i, BigInteger.valueOf(1), p)));
        }
        //result should be 3
        System.out.println(l);
    }


}
