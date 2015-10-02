package com.marasm.tape;
import com.marasm.ppc.PPC;
import com.marasm.ppc.PPCDevice;
import com.marasm.ppc.Variable;

import java.io.*;

/**
 * Created by sr3u on 07.09.15.
 */
public class Tape extends PPCDevice
{
    int maxSize=188416;
    Variable tape[]=new Variable[maxSize];
    String storageFile;
    final String ctrlPort="64.0";
    final String addressPort="64.1";
    final String dataPort="64.2";
    Variable address=new Variable(0);
    public String jarLocation()
    {
        String path=this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        if(path.endsWith("!/")){path=path.substring(0,path.length()-2);}
        String fileName=path.substring(path.lastIndexOf(File.separatorChar) + 1);
        if(fileName.contains(".jar")){path=path.substring(0, path.lastIndexOf(File.separatorChar)+1).trim();}
        if(path.startsWith("file:")){path=path.substring(5);}
        return path.trim();
    }
    @Override public String manufacturer(){return "marasm";}
    @Override
    public void connected()
    {
        System.out.println(tapeLogo);
        storageFile=jarLocation()+"tape.txt";
        System.out.println("Tape: storage: " + storageFile);
        load();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                save();
            }
        });
        PPC.connect(new Variable(ctrlPort), this);
        PPC.connect(new Variable(addressPort), this);
        PPC.connect(new Variable(dataPort), this);
    }
    @Override
    public Variable in(Variable port)
    {
        switch (port.toString())
        {
            case ctrlPort:
                return ctrlIn();
            case addressPort:
                return address;
            case dataPort:
                return tape[address.intValue()%tape.length];
        }
        return new Variable();
    }
    @Override
    public void out(Variable port,Variable data)
    {
        switch (port.toString())
        {
            case ctrlPort:
                ctrlOut(data);return;
            case addressPort:
                Variable delay=address.sub(data);
                address=data;
                try{Thread.sleep(Math.abs(delay.longValue()));}catch(InterruptedException e){}
                return;
            case dataPort:
                tape[address.intValue()%tape.length]=data;
                return;
        }
    }
    void load(int i,String str)
    {
        if(i>=maxSize){return;}
        tape[i]=new Variable(str);
    }
    void load()
    {
        try(BufferedReader br = new BufferedReader(new FileReader(storageFile))) {
            int i=0;
            for(String line; (line = br.readLine()) != null;){load(i, line);i++;}
        } catch (IOException e) {
            System.out.println("Tape: Cannot load storage from "+storageFile+"\nWill create new");
            return;
        }
    }
    void save()
    {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(storageFile))) {
            for(Variable v:tape)
            {
                if(v==null){v=new Variable();}
                bw.write(v.toString()+"\n");
            }
        } catch (IOException e) {
            System.out.println("Tape: Cannot load storage from "+storageFile+"\nWill create new");
            return;
        }
    }
    static public void main(String args[]){}
    static String tapeLogo=""+
            ".------------------------.\n" +
            "|\\\\//////// 188416 cells |\n" +
            "| \\/  __  ______  __     |\n" +
            "|    /  \\|\\.....|/  \\    |\n" +
            "|    \\__/|/_____|\\__/    |\n" +
            "| A                      |\n" +
            "|    ________________    |\n" +
            "|___/_._o________o_._\\___|";
}
