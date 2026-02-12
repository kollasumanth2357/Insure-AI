import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner s=new Scanner(System.in);
        int T=s.nextInt();
        int t=1;
        while(T-->0)
        {
            int n=s.nextInt();
            int a[][]=new int[n][n];
            for(int i=0;i<n;i++)
            {
                for(int j=0;j<n;j++)
                {
                    a[i][j]=s.nextInt();
                }
            }
            for(int i=0;i<n;i++)
            {
                for(int j=i+1;j<n;j++)
                {
                    int temp=a[i][j];
                    a[i][j]=a[j][i];
                    a[j][i]=temp;
                }
            }
            for(int i=0;i<n;i++)
            {
                int l=0,r=n-1;
                while(l<r)
                {
                    int temp=a[i][l];
                    a[i][l]=a[i][r];
                    a[i][r]=temp;
                    l++;
                    r--;
                }
            }
            System.out.println("Test Case #"+ t +":");
            for(int i=0;i<n;i++)
            {
                for(int j=0;j<n;j++)
                {
                    System.out.print(a[i][j]);
                    if(j!=n-1)System.out.print(" ");
                }
                System.out.println();
            }
            t++;
        }
       
    }
}