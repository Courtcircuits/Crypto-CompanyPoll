import java.math.BigInteger;
import java.util.Random;

public class FindP extends Thread{
    private BigInteger p;
    private BigInteger pPrime;

    public void run(){
        p = BigInteger.probablePrime(CryptoUtils.getL(), new Random());
        pPrime = p.subtract(BigInteger.ONE).divide(BigInteger.TWO);

        if(pPrime.isProbablePrime(100)){
            CryptoUtils.setP(p);

        }
        this.interrupt();
    }

}