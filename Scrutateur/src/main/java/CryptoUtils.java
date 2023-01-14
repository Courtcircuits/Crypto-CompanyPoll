import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class CryptoUtils {

    private static BigInteger polynome(BigInteger[] coefs, BigInteger x){
        BigInteger result = BigInteger.ZERO;
        for(int i=0; i< coefs.length; i++){
            result = result.add((x.pow(i)).multiply(coefs[coefs.length-1-i]));
        }
        return result;
    }

    /**
     * KeyGen of Elgamal
     * @param t -> number of minimum available KeyHolder
     * @param n -> number of shares
     * @param high -> the highest coefficient for polynome
     * @param bit -> bit length of the key
     * @return publicKey and secretKey
     */
    public static Key[] KeyGen(int bit, int t, BigInteger high, int n){
        //choose p
        BigInteger p = BigInteger.probablePrime(bit, new Random());
        BigInteger q = p.subtract(BigInteger.ONE).divide(BigInteger.TWO);
        while (!q.isProbablePrime(100)){
            p = BigInteger.probablePrime(bit, new Random());
            q = p.subtract(BigInteger.ONE).divide(BigInteger.TWO);
        }

        //find a generator g of order q  <=> g^q % p == 1
        boolean found = false;
        BigInteger g = new BigInteger(bit, new Random());
        while (!found){
            BigInteger remainder = g.modPow(q, p); //g^q % p
            if(!(g.compareTo(q) < 0 && g.compareTo(BigInteger.ONE)>0)){ //g belongs to [0, p] since it's an element of Zp
                g = new BigInteger(bit, new Random());
            }else{
                if(remainder.equals(BigInteger.ONE)){ // g^q % p == 1
                    found = true;
                }else{
                    g = new BigInteger(bit, new Random());
                }
            }
        }

        BigInteger a; //secretKey
        do{
            a = new BigInteger(bit, new Random());
        }while (a.compareTo(q)>=0);


        BigInteger b = g.mod(a);

        //polynome generation
        BigInteger[] coefs = new BigInteger[t];
        for(int i=0; i<t-1; i++){
            do{
                coefs[i] = new BigInteger(bit, new Random());
            }while (a.compareTo(high)>=0);
        }
        coefs[t-1] = a;

        BigInteger[] shares = new BigInteger[n];

        for(int i=0; i<n; i++){
            BigInteger x;
            do{
                x = new BigInteger(bit, new Random());
            }while (x.compareTo(high)>=0);
            shares[i] = polynome(coefs, x);
            System.out.println(shares[i]);
        }

        return new Key[]{new PublicKey(p, g, b), new SecretKey(a)};
    }

    public static BigInteger[] Encrypt(BigInteger m, PublicKey key){
        BigInteger q = (key.getP().subtract(BigInteger.ONE)).divide(BigInteger.TWO);
        BigInteger k;
        do{
            k = new BigInteger(q.bitLength(), new Random());
        }while (k.compareTo(q)>=0);
        BigInteger c1 = key.getG().modPow(k,key.getP());
        BigInteger c2 = key.getH().modPow()
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(KeyGen(5, 3, BigInteger.valueOf(10), 5)));
    }
}
