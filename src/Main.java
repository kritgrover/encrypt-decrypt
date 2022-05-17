import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Class works with command-line arguments. The program can parse five arguments:
 * -mode, -key and -data -in -out.
 * The first argument should determine the program’s mode
 * (enc for encryption, dec for decryption).
 * The second argument is an integer key to modify the message using the specified algorithm,
 * The third argument is a text or ciphertext to encrypt or decrypt.
 * Arguments -in and -out to specify the full name of a file to read data and to write the result.
 * The necessary algorithm should be specified by an argument (-alg).
 * The first algorithm should be named shift, the second one should be named unicode.
 * If there is no -alg you should default it to shift.
 */
public class Main {

    //storing values as final variables for default check and argument value indexing
    static final String ALG = "-alg";
    static final String SHIFT = "shift";
    static final String UNICODE = "unicode";
    static final String ENC = "enc";
    static final String DEC = "dec";
    static final String MODE = "-mode";
    static final String KEY = "-key";
    static final String DATA = "-data";
    static final String IN = "-in";
    static final String OUT = "-out";
    static final String CONSOLE = "console";
    static final String ERROR = "Error";

    static final String encrypt = "abcdefghijklmnopqrstuvwxyz";

    //running program and handling exceptions
    public static void main(String[] args) {
        try {
            readArgs(args);
        } catch (Exception e) {
            e.printStackTrace();
            printToConsole(ERROR);
        }
    }

    /**
     * Method read command line args and write them to HashMap with name of arg as a key and its parameter as a value.
     * @param args - command line parameters
     * @throws IOException
     */
    public static void readArgs(String[] args) throws IOException {

        //storing values to HashMap
        HashMap<String, String> parameters = new HashMap<>();
        for (int i = 1; i < args.length; i++) {
            if (i % 2 != 0) {
                parameters.put(args[i - 1], args[i]);
            }
        }

        //initializing variables and defaulting wherever required
        final var alg = parameters.getOrDefault(ALG, SHIFT);
        final var mode = parameters.getOrDefault(MODE, ENC);
        final var key = Integer.parseInt(parameters.getOrDefault(KEY, "0"));
        final var in = parameters.getOrDefault(IN, null);
        final var data = parameters.getOrDefault(DATA, null);
        final var out = parameters.getOrDefault(OUT, CONSOLE);

        if (out.equals(CONSOLE)) {
            printToConsole(readData(data, key, mode, in, alg));
        } else {
            writeToFile(out, readData(data, key, mode, in, alg));
        }

    }

    /**
     * Method select how to read input data, read them and choose encrypt/decrypt mode
     * @param data - input data
     * @param key - encrypt/decrypt key
     * @param mode - encrypt/decrypt mode
     * @param in - path to read input data (optional)
     * @return - string with encrypt/decrypt data
     * @throws IOException
     */
    private static String readData(
            String data,
            int key,
            String mode,
            String in,
            String alg) throws IOException {

        String cryptData;
        
        /*Logic to check for input file
         *If missing, read from command line 
        */ 
        if (data != null && in != null || data != null && in == null) {
            cryptData = data;
        } else if (in != null && data == null ) {
            cryptData = readFromFile(in);
        } else {
            throw new RuntimeException();
        }

        /*Logic to check for specific algorithm
         *If missing, default to shift
        */
        if (alg != null && alg.equals(UNICODE)) {
            return cryptData(cryptData, key, mode, UNICODE);
        } else if (alg != null && alg.equals(SHIFT)) {
            return cryptData(cryptData, key, mode, SHIFT);
        } else {
            throw new RuntimeException();
        }
    }

    /**
     * Method shift data as String and encrypt or decrypt them
     * @param data - input data
     * @param key - encrypt/decrypt key
     * @param mode - encrypt/decrypt mode
     * @return
     */
    private static String cryptData(String data, int key, String mode, String alg) {
        char[] sb = new char[data.length()];
        data = data.trim();
        for (var i = 0; i < data.length(); i++) {
            var tmpChar = data.charAt(i);
            if (mode.equals(ENC)) {
                sb[i] = encrypt(tmpChar, key, alg);
            } else {
                sb[i] = decrypt(tmpChar, key, alg);
            }
        }
        return String.valueOf(sb);
    }

    /**
     * Method encrypt char
     * @param tmpChar char to encrypt
     * @param key shift encrypting
     * @return
     */
    private static char encrypt(char tmpChar, int key, String alg) {

        if (UNICODE.equals(alg)) {
            return ((char) (tmpChar + key));
        } else {
            var index = encrypt.indexOf(Character.toLowerCase(tmpChar));
            var enChar = index == -1 ? tmpChar : encrypt.charAt((index + key) % encrypt.length());
            return Character.isUpperCase(tmpChar) ? Character.toUpperCase(enChar) : enChar;
        }
    }

    /**
     * Method decrypt char
     * @param tmpChar char to decrypt
     * @param key shift decrypting
     * @return
     */
    private static char decrypt(char tmpChar, int key, String alg) {
        if (UNICODE.equals(alg)) {
            return ((char) (tmpChar - key));
        } else {
            var index = encrypt.indexOf(Character.toLowerCase(tmpChar));
            if (index == -1) {
                return tmpChar;
            } else if (index - key < 0) {
                var enChar = encrypt.charAt(encrypt.length() - Math.abs(index - key));
                return Character.isUpperCase(tmpChar) ? Character.toUpperCase(enChar) : enChar;
            } else {
                var enChar = encrypt.charAt(index - key);
                return Character.isUpperCase(tmpChar) ? Character.toUpperCase(enChar) : enChar;
            }
        }
    }

    /**
     * Method read file from input path
     * @param in path to read
     * @return data as String
     * @throws IOException
     */
    private static String readFromFile(String in) throws IOException {
        return new String(Files.readAllBytes(Paths.get(in)));
    }

    /**
     * Method write data to file for input path
     * @param out path to write data to file
     * @param data data to write
     * @throws IOException
     */
    private static void writeToFile(String out, String data) throws IOException {
        FileWriter writer = new FileWriter(out);
        writer.write(data);
        writer.flush();
        writer.close();
    }

    /**
     * Method print data to console
     * @param message data to print
     */
    private static void printToConsole(String message) {
        System.out.println(message);
    }

}
