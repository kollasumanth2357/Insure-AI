import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner s=new Scanner(System.in);
        int T=s.nextInt();
        while(T-->0)
        {
            int N=s.nextInt();
            int[] arr=new int[N];
            for(int i=0;i<N;i++)
            {
                arr[i]=s.nextInt();
            }
            for(int i=N-1;i>0;i--)
            {
                int minIndex=i;
                for(int j=i-1;j>=0;j--)
                {
                    if(arr[j]>=arr[minIndex])
                    {
                        minIndex=j;
                    }
                }
                int temp=arr[i];
                arr[i]=arr[minIndex];
                arr[minIndex]=temp;
                
                System.out.print(minIndex+" ");
            }
            System.out.println();
        }
    }
}