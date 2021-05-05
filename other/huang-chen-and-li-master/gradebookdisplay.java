import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import java.util.regex.Pattern;

import javax.crypto.NoSuchPaddingException;


public class gradebookdisplay {
    static Gradebook gradebook;

    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, ClassNotFoundException, IOException {
        if (args.length < 6) {
            err();
        }

        if (args[0].equals("-N") && args[2].equals("-K")) {
            String filename = args[1];
            String key = args[3];
           
            try{
                gradebook = new Gradebook(filename, key);
            } catch (Exception e){
                err();
            }

            if (args[4].equals("-PA")) {
                String assignment_name = null, order = null;
                for (int i = 5; i < args.length; i ++) {
                    if (args[i].equals("-AN")) {
                        assignment_name = args[i + 1];
                        i++;
                    } else if (args[i].equals("-A")) {
                        if (order == null){
                            order = "a";
                        } else {
                            err();
                        }
                    } else if (args[i].equals("-G")) {
                        if (order == null){
                            order = "g";
                        } else {
                            err();
                        }
                    } else {
                        err();
                    }
                }
                String regex = "[0-9A-Za-z_.]+";
                Pattern p = Pattern.compile(regex);
                if (assignment_name != null && order != null && p.matcher(assignment_name).matches()) {
                    gradebook.printAssignment(assignment_name,order);
                } else {
                    err();
                }
            } else if (args[4].equals("-PS")) {
                String firstname = null, lastname = null;
                for (int i = 5; i < args.length ; i ++) {
                    if (args[i].equals("-FN")) {
                        firstname = args[i + 1];
                        i++;
                    } else if (args[i].equals("-LN")) {
                        lastname = args[i + 1];
                        i++;
                    } else {
                        err();
                    }
                }
                String regex = "[A-Za-z_.]+";
                Pattern p = Pattern.compile(regex);
                if (firstname != null && lastname != null && p.matcher(firstname).matches() && p.matcher(lastname).matches()) {
                    gradebook.printStudent(firstname, lastname);
                } else {
                    err();
                }
            } else if (args[4].equals("-PF")) {
                String order = null;
                for (int i = 5; i < args.length; i ++) {
                    if (args[i].equals("-A")) {
                        if (order == null){
                            order = "a";
                        } else {
                            err();
                        }
                    } else if (args[i].equals("-G")) {
                        if (order == null){
                            order = "g";
                        } else {
                            err();
                        }
                    } else {
                        err();
                    }
                }
                if (order != null) {
                    gradebook.printFinal(order);
                } else {
                    err();
                }
            } else {
                err();
            }
        } else {
            err();
        }
    }


    static void err(){
        System.out.println("invalid");
        System.exit(255);
    }
}
