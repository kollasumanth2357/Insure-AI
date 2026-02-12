import java.io.*;
import java.util.*;

public class Main {
    public static void TH(int n,char src,char dest,char temp)
    {
        if(n==0)
            return;
        TH(n-1,src,temp,dest);
        System.out.println("Move "+n+" from "+src+" to "+dest);
        TH(n-1,temp,dest,src);
    }

    public static void main(String[] args) {
        Scanner s=new Scanner(System.in);
        int n=s.nextInt();
        while(n-->0)
        {
            int a=s.nextInt();
            char src='A';
            char dest='C';
            char temp='B';
            System.out.println((int)Math.pow(2,a)-1);
            TH(a,src,dest,temp);
        }
    }
}