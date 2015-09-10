package com.marasm.tapetest;
import com.marasm.ppc.CTRL;
import com.marasm.ppc.PPC;
import com.marasm.ppc.Variable;
import com.marasm.tape.Tape;

/**
 * Created by sr3u on 07.09.15.
 */
public class TapeTest
{
    static public void main(String args[])
    {
        Tape t=new Tape();
        t.connected();
        Variable ctrlPort=new Variable("64.0");
        PPC.out(ctrlPort, CTRL.GETMAN);
        Variable v=new Variable("-1");
        int i=0;
        while (!v.equals(new Variable(0)))
        {
            v=PPC.in(ctrlPort);
            System.out.print((char)v.intValue());
            PPC.out(new Variable("64.1"), new Variable(i));
            i++;
            PPC.out(new Variable("64.2"),v);
        }PPC.out(new Variable("64.2"),new Variable(0));
    }
}
