import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class CryptoUtils {

    private static List<KeyHolder> holders = new ArrayList<>();

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
            }while (x.compareTo(high)>0);

            holders.add(new KeyHolder(x, polynome(coefs, x)));

        }

        return new Key[]{new PublicKey(p, g, b), new SecretKey(a)};
    }

    private static BigInteger tirageX(BigInteger p) {
        boolean found = false;
        BigInteger pPrime = p.subtract(BigInteger.ONE).divide(BigInteger.TWO);
        BigInteger x = new BigInteger(pPrime.bitLength(), new Random());
        while (!found) {
            if (x.compareTo(pPrime) < 0 && x.compareTo(BigInteger.ZERO) >= 0) {
                found = true;
            } else {
                x = new BigInteger(pPrime.bitLength(), new Random());
            }
        }

        return x;
    }
    private static BigInteger[] produireC(BigInteger g, BigInteger r, BigInteger p, BigInteger h, int m) {

        BigInteger c[] = {BigInteger.ZERO, BigInteger.ZERO};
        c[0] = g.modPow(r, p);
        BigInteger pt1 = g.modPow(BigInteger.valueOf(m), p);
        BigInteger pt2 = h.modPow(r, p);
        c[1] = pt1.multiply(pt2);

        return c;
    }

    private static BigInteger tirageR(BigInteger p) {
        return tirageX(p);
    }
    public static BigInteger[] Encrypt(PublicKey pk, int m) {
        BigInteger r = tirageR(pk.getP());
        return produireC(pk.getG(), r, pk.getP(), pk.getH(), m);
    }

    public static BigInteger Decrypt(BigInteger[] c,  PublicKey key){
        ArrayList<BigInteger> deltas = new ArrayList<>();
        int n=holders.size();

        for(int i=0; i<n; i++){
            int k=0;
            BigInteger delta = BigInteger.ONE;
            do{
                if(k!=i){
                    delta = delta.multiply( holders.get(k).getX().divide(holders.get(k).getX().subtract(holders.get(i).getX()))).mod(key.getP());
                }
                k++;
            }while (k<n);
            deltas.add(delta);
        }

        BigInteger d= BigInteger.ONE;
        for(int i=0; i<n;i++){
            d = d.multiply(holders.get(i).computeD(c[0], key.getP()).modPow(deltas.get(i),key.getP()));
        }
        BigInteger m = d.modInverse(key.getP()).multiply(c[1]).mod(key.getP());
        return m;
    }

    public static void main(String[] args) {
        Key[] keys = KeyGen(5,3,BigInteger.valueOf(10), 5);

        PublicKey pk = (PublicKey) keys[0];
        System.out.println(pk);
        BigInteger[] c = Encrypt(pk, 4);
        //BigInteger m = Decrypt(c, pk);
        //System.out.println(m);
    }
}
