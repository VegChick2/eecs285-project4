package eecs285.proj4.qiaotian;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.exit;
import static java.lang.System.out;

public class server {

    public static void main(String args[]) {

        server server_obj = new server();


    }

    private ClientServerSocket theServer;
    private Map<String, category> budgets = new HashMap<String, category>();
    private DecimalFormat df = new DecimalFormat("0.00");
    private String instruction;
    private String filename;

    private  server() {
        start();

        while (true) {
            instruction = getinstruction();
            String response = "";
            if (instruction.charAt(0) == 'l')//getlist
            {

                for (Map.Entry<String, category> entry : budgets.entrySet()) {
                    response += '%';
                    response += entry.getKey();
                    response += '#';
                }

            } else if (instruction.charAt(0) == 'g') {//get balance
                String args[] = new String[1];
                getargs(args);

                category target = budgets.get(args[0]);

                response = '%' + df.format(target.balance) + '#';


            } else if (instruction.charAt(0) == 't') {//transaction

                String args[] = new String[3];//0:category 1:merchant 2:amount


                getargs(args);

                category target = budgets.get(args[0]);
                String merchant = args[1];
                double amount = Double.parseDouble(args[2]);


                target.balance -= amount;
                target.transactions.add(args[0] + ':' + merchant + ':' + df.format(amount));
                response = '%' + df.format(target.budget) + '#';


            } else if (instruction.charAt(0) == 's') {//save
                save();
                response = "%save#";

            } else if (instruction.charAt(0) == 'e') {//exit

                theServer.sendString("%exit#");
                exit(0);
            } else if (instruction.charAt(0) == 'r') {//readfile
                String args[] = new String[1];
                getargs(args);
                filename = args[1];
                readfile();
                response = "%read#";
            } else {
                out.println("%unknown#");
                response = "%unknown#";
            }

            theServer.sendString(response);
        }

    }

    void readfile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            String line;
            int catsize=-1;
            int j=0;
            while ((line = reader.readLine()) != null) {



                if(catsize==-1)//first line
                catsize=Integer.parseInt(line);

                else if(j<catsize)//categories
                {


                    String cat="";
                    String bud="";

                    int i=0;
                    for(;line.charAt(i)!=':';i++)
                    {
                        cat+=line.charAt(i);
                    }
                    i++;
                    for(;i<line.length();i++)
                    {
                        bud+=line.charAt(i);
                    }


                    budgets.put(new String(cat),new category(Double.parseDouble(bud)));




                    j++;

                }
                else//transactions
                {
                    String cat="";
                    String mer="";
                    String amount="";


                    int i=0;
                    for(;line.charAt(i)!=':';i++)
                    {
                        cat+=line.charAt(i);
                    }
                    i++;
                    for(;line.charAt(i)!=':';i++)
                    {
                        mer+=line.charAt(i);
                    }
                    for(;i<line.length();i++)
                    {
                        amount+=line.charAt(i);
                    }


                    category target = budgets.get(cat);
                    target.transactions.add(line);
                    target.balance-=Double.parseDouble(amount);



                }





            }
            reader.close();
        } catch (Exception ioe) {
            ioe.printStackTrace();
            exit(1);
        }


    }


    void save() {
        try {
            FileWriter writer = new FileWriter(filename);
            writer.write(budgets.size());//number
            for (Map.Entry<String, category> entry : budgets.entrySet()) {//category


                writer.write(entry.getKey()+':'+entry.getValue().budget);


            }
            for (Map.Entry<String, category> entry : budgets.entrySet()) {//transactions


                for(int k=0;k<entry.getValue().transactions.size();k++)
                writer.write(entry.getValue().transactions.elementAt(k));


            }
            writer.close();
        } catch (Exception ioe) {
            ioe.printStackTrace();
            exit(1);
        }

    }


    private void start() {
        theServer = new ClientServerSocket("127.0.0.1", 45000);
        theServer.startServer();

    }

    private void getargs(String args[]) {
        int argindex = 0;
        int i = 1;


        while (argindex < args.length) {
            if (instruction.charAt(i) == '%') {
                i++;
            } else if (instruction.charAt(i) == '#') {
                i++;
                argindex++;
            } else {
                i++;
                args[argindex] = args[argindex] + instruction.charAt(i);
            }
        }//grt arguments

    }

    private String getinstruction() {
        return theServer.recvString();

    }


}
