import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AppExternalRun {




	public static void main(String[] args) {
		  String result;
		  try {
		    Runtime rt = Runtime.getRuntime();
		    rt.exec("notepad.exe");

		    Process p = rt.exec("CMD.EXE /C DIR c:/windows/");
		    InputStream is = p.getInputStream();
		    InputStreamReader isr = new InputStreamReader(is);
		    BufferedReader br = new BufferedReader(isr);
		      while ((result = br.readLine()) != null) {
		      System.out.println(result);
		      }

		      rt.exec("java -jar C:/allabout/FreeLife.jar");
		  } catch (IOException ex) {
		    ex.printStackTrace();
		  }
		}

}
